package net.wapic.wpcmod.features

import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.events.skyblock.KuudraEvents
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.SackUtils
import net.wapic.wpcmod.util.Utils

class AutoGFS {

	private val config get() = WpcMod.config

	init {
		KuudraEvents.START.register(::onKuudraStart)
		DungeonEvents.START.register(::onDungeonStart)
	}

	private fun onKuudraStart() {
		if (Utils.getLocation() != Island.KUUDRA || !config.kuudraConfig.autoGfs) return
		SackUtils.getFromSack("ENDER_PEARL", 16)
	}

	private fun onDungeonStart() {
		if (Utils.getLocation() != Island.DUNGEON) return
		if (config.dungeonConfig.autoGFS.enderPearl) SackUtils.getFromSack("ENDER_PEARL", 16)
		if (config.dungeonConfig.autoGFS.spiritLeap) SackUtils.getFromSack("SPIRIT_LEAP", 16)
		if (config.dungeonConfig.autoGFS.superboomTNT) SackUtils.getFromSack("SUPERBOOM_TNT", 64)
	}
}