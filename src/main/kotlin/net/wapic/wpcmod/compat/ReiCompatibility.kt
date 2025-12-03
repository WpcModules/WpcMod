package net.wapic.wpcmod.compat

import me.shedaniel.rei.api.client.config.ConfigManager
import net.fabricmc.loader.api.FabricLoader

object ReiCompatibility {

	fun isModLoaded(): Boolean = FabricLoader.getInstance().isModLoaded("roughlyenoughitems")
	fun isOverlayVisible(): Boolean {
		if (isModLoaded()) return ConfigManager.getInstance().config.isOverlayVisible
		return false
	}

	fun setOverlayVisible(value: Boolean) {
		if (isModLoaded()) ConfigManager.getInstance().config.isOverlayVisible = value
	}
}