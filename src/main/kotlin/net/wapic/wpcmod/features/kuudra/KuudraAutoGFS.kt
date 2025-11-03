package net.wapic.wpcmod.features.kuudra

import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.KuudraEvents
import net.wapic.wpcmod.util.SackUtils

object KuudraAutoGFS {
	private val config get() = WpcMod.config.kuudra

	fun init() {
		KuudraEvents.START.register(::onKuudraStart)
	}

	fun onKuudraStart() {
		if (config.autoGfs) SackUtils.getFromSack("ENDER_PEARL", 16)
	}
}