package net.wapic.wpcmod.features.instance

import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.events.skyblock.KuudraEvents
import net.wapic.wpcmod.util.SackUtils

object AutoGFS {
	private val config get() = WpcMod.config
	fun init() {
		KuudraEvents.START.register {
			if (config.kuudra.autoGfs) SackUtils.queueGetFromSack("ENDER_PEARL", 16)
		}

		DungeonEvents.START.register {
			if(!config.dungeon.autoGFS.enabled) return@register
			config.dungeon.autoGFS.items.forEach { SackUtils.queueGetFromSack(it.name, it.maxStackSize) }
		}
	}

	enum class DungeonSackItems(val label: String, val maxStackSize: Int) {
		ENDER_PEARL("Ender Pearl", 16),
		SUPERBOOM_TNT("Superboom TNT", 64),
		SPIRIT_LEAP("Spirit Leap", 16);

		override fun toString() = "§f$label"
	}
}