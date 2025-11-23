package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.wapic.wpcmod.features.chat.CompactChat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ChatHud.class, priority = Integer.MAX_VALUE)
public abstract class ChatHudMixin {
	private final @Unique ThreadLocal<CompactChat.@Nullable Message> CURRENT = new ThreadLocal<>();

	@ModifyVariable(
			method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
			at = @At("HEAD"),
			argsOnly = true
	)
	public Text compacting$compactText(Text text) {
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
			method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
			at = @At(
					value = "NEW",
					target = "(ILnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)Lnet/minecraft/client/gui/hud/ChatHudLine;"
			)
	)
	public ChatHudLine compacting$associateHudLine(int creationTick, Text text, MessageSignatureData messageSignatureData, MessageIndicator messageIndicator, Operation<ChatHudLine> original) {
		var line = original.call(creationTick, text, messageSignatureData, messageIndicator);
		var message = CURRENT.get();
		if (message != null) {
			message.setLastLine(line);
		}
		return line;
	}

	@WrapOperation(
			method = "addVisibleMessage",
			at = @At(
					value = "NEW",
					target = "(ILnet/minecraft/text/OrderedText;Lnet/minecraft/client/gui/hud/MessageIndicator;Z)Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;"
			)
	)
	public ChatHudLine.Visible compacting$associateVisibleLine(int tick, OrderedText text, MessageIndicator indicator,
															   boolean endOfEntry, Operation<ChatHudLine.Visible> original) {
		var visible = original.call(tick, text, indicator, endOfEntry);
		var message = CURRENT.get();
		if (message != null) {
			message.getLastVisible().add(visible);
		}
		return visible;
	}

	@Inject(method = "clear", at = @At("TAIL"))
	public void compacting$clearMessages(boolean clearHistory, CallbackInfo ci) {
		CompactChat.clear();
		CURRENT.remove();
	}

	@Inject(method = "refresh", at = @At("HEAD"))
	public void compacting$createLineMap(
			CallbackInfo ci,
			@Share(namespace = "compacting", value = "lines") LocalRef<Map<ChatHudLine, CompactChat.Message>> lines
	) {
		lines.set(CompactChat.buildLineCache());
	}

	@WrapOperation(
			method = "refresh",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/ChatHud;addVisibleMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V"
			)
	)
	public void compacting$wrapRefresh(
			ChatHud instance,
			ChatHudLine message,
			Operation<Void> original,
			@Share(namespace = "compacting", value = "lines") LocalRef<Map<ChatHudLine, CompactChat.Message>> lines
	) {
		var cache = lines.get();
		CURRENT.set(cache.get(message));
		original.call(instance, message);
	}
}