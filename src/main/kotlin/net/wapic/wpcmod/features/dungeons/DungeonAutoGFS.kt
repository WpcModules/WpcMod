package net.wapic.wpcmod.features.dungeons

import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.util.SackUtils

object DungeonAutoGFS {

	private val config get() = WpcMod.config.dungeon.autoGFS

	fun init() {
		DungeonEvents.START.register {
			if (config.enderPearl) SackUtils.getFromSack("ENDER_PEARL", 16)
			if (config.spiritLeap) SackUtils.getFromSack("SPIRIT_LEAP", 16)
			if (config.superboomTNT) SackUtils.getFromSack("SUPERBOOM_TNT", 64)
		}
	}
}