package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;
import net.wapic.wpcmod.WpcMod;
import net.wapic.wpcmod.features.chat.CompactChat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ChatComponent.class, priority = Integer.MAX_VALUE)
public abstract class ChatComponentMixin {
	private final @Unique ThreadLocal<CompactChat.@Nullable Message> CURRENT = new ThreadLocal<>();

	@ModifyExpressionValue(method = {"addMessageToQueue", "addMessageToDisplayQueue"}, at = @At(value = "CONSTANT", args = "intValue=100"))
	private int modifyMaxChatSize(int value) {
		return WpcMod.config.getChat().getLongerChatHistory() ? 10000 : value;
	}

	@ModifyVariable(
			method = "addMessage",
			at = @At("HEAD"),
			argsOnly = true
	)
	public Component compacting$compactText(Component contents) {
		var message = CompactChat.compact(contents);
		if (message == null) {
			CURRENT.remove();
			return contents;
		}
		CURRENT.set(message);
		if (message.shouldCompact()) {
			return message.getTextWithCounter();
		}
		return contents;
	}

	@WrapOperation(
			method = "addMessage",
			at = @At(
					value = "NEW",
					target = "(ILnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)Lnet/minecraft/client/multiplayer/chat/GuiMessage;"
			)
	)
	public GuiMessage compacting$associateHudLine(int addedTime, Component content, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, Operation<GuiMessage> original) {
		var line = original.call(addedTime, content, signature, source, tag);
		var message = CURRENT.get();
		if (message != null) {
			message.setLastLine(line);
		}
		return line;
	}

	@WrapOperation(
			method = "addMessageToDisplayQueue",
			at = @At(
					value = "NEW",
					target = "(Lnet/minecraft/client/multiplayer/chat/GuiMessage;Lnet/minecraft/util/FormattedCharSequence;Z)Lnet/minecraft/client/multiplayer/chat/GuiMessage$Line;"
			)
	)
	public GuiMessage.Line compacting$associateVisibleLine(GuiMessage parent, FormattedCharSequence content, boolean endOfEntry, Operation<GuiMessage.Line> original) {
		var visible = original.call(parent, content, endOfEntry);
		var message = CURRENT.get();
		if (message != null) {
			message.getLastVisible().add(visible);
		}
		return visible;
	}

	@Inject(method = "clearMessages", at = @At("TAIL"))
	public void compacting$clearMessages(boolean history, CallbackInfo ci) {
		CompactChat.clear();
		CURRENT.remove();
	}

	@Inject(method = "refreshTrimmedMessages", at = @At("HEAD"))
	public void compacting$createLineMap(
			CallbackInfo ci,
			@Share(namespace = "compacting", value = "lines") LocalRef<Map<GuiMessage, CompactChat.Message>> lines
	) {
		lines.set(CompactChat.buildLineCache());
	}

	@WrapOperation(
			method = "refreshTrimmedMessages",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessageToDisplayQueue(Lnet/minecraft/client/multiplayer/chat/GuiMessage;)V"
			)
	)
	public void compacting$wrapRefresh(
			ChatComponent instance,
			GuiMessage message,
			Operation<Void> original,
			@Share(namespace = "compacting", value = "lines") LocalRef<Map<GuiMessage, CompactChat.Message>> lines
	) {
		var cache = lines.get();
		CURRENT.set(cache.get(message));
		original.call(instance, message);
	}
}