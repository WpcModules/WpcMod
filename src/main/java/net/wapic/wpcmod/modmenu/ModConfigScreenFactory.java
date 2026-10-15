package net.wapic.wpcmod.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.minecraft.client.gui.screens.Screen;
import net.wapic.wpcmod.config.ConfigManager;
import net.wapic.wpcmod.util.MC;

public class ModConfigScreenFactory implements ConfigScreenFactory<Screen> {

	@Override
	public Screen create(Screen parent) {
		ConfigManager.INSTANCE.openConfig(MC.INSTANCE.getInstance());
		return parent;
	}
}
