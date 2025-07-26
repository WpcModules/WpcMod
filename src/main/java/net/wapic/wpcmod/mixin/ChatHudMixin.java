package net.wapic.wpcmod.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.wapic.wpcmod.WpcMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatHud.class)
public class ChatHudMixin {
	@ModifyConstant(method = {"addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", "addVisibleMessage"}, constant = @Constant(intValue = 100))
	private int injected(int value) {
		return (int) WpcMod.config.getChatConfig().getChatHistoryLength();
	}
}