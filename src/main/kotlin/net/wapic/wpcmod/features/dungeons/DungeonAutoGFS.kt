package net.wapic.wpcmod.features.dungeons

import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.util.SackUtils

object DungeonAutoGFS {

	private val config get() = WpcMod.config.dungeon.autoGFS

	fun init() {
		DungeonEvents.START.register(::onDungeonStart)
	}

	fun onDungeonStart() {
		if (!config.enabled) return
		config.items.forEach {
			SackUtils.getFromSack(it.name, it.maxStackSize)
		}
	}

	enum class DungeonSackItems(val label: String, val maxStackSize: Int) {
		ENDER_PEARL("Ender Pearl", 16),
		SUPERBOOM_TNT("Superboom TNT", 64),
		SPIRIT_LEAP("Spirit Leap", 16);

		override fun toString(): String {
			return "§f$label"
		}
	}
}