package net.wapic.wpcmod.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import net.wapic.wpcmod.WpcMod;
import net.wapic.wpcmod.features.chat.CompactChat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(value = ChatHud.class, priority = Integer.MAX_VALUE)
public abstract class ChatHudMixin {

	@Shadow
	@Final
	private List<ChatHudLine> messages;

	@Shadow
	protected abstract void refresh();

	@ModifyConstant(method = {"addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", "addVisibleMessage"}, constant = @Constant(intValue = 100))
	private int injected(int value) {
		return WpcMod.config.getChat().getLongerChatHistory() ? 10000 : value;
	}

	@ModifyVariable(
			method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
			at = @At("HEAD"),
			argsOnly = true
	)
	public Text addMessage(final Text message) {
		Text text = CompactChat.INSTANCE.compactMessage(message, messages);
		if (!text.getString().equals(message.getString())) {
			this.refresh();
		}
		return text;
	}

	@Inject(method = "clear", at = @At("HEAD"))
	public void clear(CallbackInfo ci) {
		CompactChat.INSTANCE.clear();
	}
}