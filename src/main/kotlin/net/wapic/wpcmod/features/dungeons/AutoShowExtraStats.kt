package net.wapic.wpcmod.features.dungeons

import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.util.Utils

object AutoShowExtraStats {

	private val isEnabled get() = WpcMod.config.dungeon.autoShowExtraStats

	fun init() {
		DungeonEvents.END.register(::onDungeonEnd)
	}

	fun onDungeonEnd() {
		if (!isEnabled) return
		Utils.addToCommandQueue("/showextrastats")
	}
}