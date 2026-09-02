package net.wapic.wpcmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.wapic.wpcmod.util.MenuScreenHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MenuScreens.ScreenConstructor.class)
public interface MenuScreenConstructorMixin<T extends AbstractContainerMenu> {

	@Inject(method = "fromPacket", at = @At("HEAD"), cancellable = true)
	private void wpcmod$openCustomMenu(Component title, MenuType<T> type, Minecraft minecraft, int containerId, CallbackInfo ci) {
		if (MenuScreenHook.openCustomMenu(title, type, minecraft, containerId)) {
			ci.cancel();
		}
	}
}