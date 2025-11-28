package net.wapic.wpcmod.compat

import me.shedaniel.rei.api.client.config.ConfigManager
import net.fabricmc.loader.api.FabricLoader

object ReiCompatibility {

	fun isModLoaded(): Boolean = FabricLoader.getInstance().isModLoaded("roughlyenoughitems")
	fun isOverlayActive(): Boolean = ConfigManager.getInstance().config.isOverlayVisible

	fun setOverlayActive(value: Boolean) {
		ConfigManager.getInstance().config.isOverlayVisible = value
	}
}