package net.wapic.wpcmod.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.minecraft.client.gui.screens.Screen;
import net.wapic.wpcmod.config.ConfigManager;

public class ModConfigScreenFactory implements ConfigScreenFactory<Screen> {

	@Override
	public Screen create(Screen parent) {
		return ConfigManager.INSTANCE.createScreen(parent);
	}
}
