package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;
import net.wapic.wpcmod.WpcMod;
import net.wapic.wpcmod.features.chat.CompactChat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ChatComponent.class, priority = Integer.MAX_VALUE)
public abstract class ChatComponentMixin {
	private final @Unique ThreadLocal<CompactChat.@Nullable Message> CURRENT = new ThreadLocal<>();

	@ModifyConstant(method = {"addMessageToQueue(Lnet/minecraft/client/GuiMessage;)V", "addMessageToDisplayQueue"}, constant = @Constant(intValue = 100))
	private int modifyMaxChatSize(int value) {
		return WpcMod.config.getChat().getLongerChatHistory() ? 10000 : value;
	}

	@ModifyVariable(
			method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
			at = @At("HEAD"),
			argsOnly = true
	)
	public Component compacting$compactText(Component text) {
		var message = CompactChat.compact(text);
		if (message == null) {
			CURRENT.remove();
			return text;
		}
		CURRENT.set(message);
		if (message.shouldCompact()) {
			return message.getTextWithCounter();
		}
		return text;
	}

	@WrapOperation(
			method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
			at = @At(
					value = "NEW",
					target = "(ILnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)Lnet/minecraft/client/GuiMessage;"
			)
	)
	public GuiMessage compacting$associateHudLine(int creationTick, Component text, MessageSignature messageSignatureData, GuiMessageTag messageIndicator, Operation<GuiMessage> original) {
		var line = original.call(creationTick, text, messageSignatureData, messageIndicator);
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
					target = "(ILnet/minecraft/util/FormattedCharSequence;Lnet/minecraft/client/GuiMessageTag;Z)Lnet/minecraft/client/GuiMessage$Line;"
			)
	)
	public GuiMessage.Line compacting$associateVisibleLine(int tick, FormattedCharSequence text, GuiMessageTag indicator,
														   boolean endOfEntry, Operation<GuiMessage.Line> original) {
		var visible = original.call(tick, text, indicator, endOfEntry);
		var message = CURRENT.get();
		if (message != null) {
			message.getLastVisible().add(visible);
		}
		return visible;
	}

	@Inject(method = "clearMessages", at = @At("TAIL"))
	public void compacting$clearMessages(boolean clearHistory, CallbackInfo ci) {
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
					target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessageToDisplayQueue(Lnet/minecraft/client/GuiMessage;)V"
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