package net.wapic.wpcmod.features.kuudra

import net.wapic.wpcmod.events.ServerTickEvent
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.KuudraUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.toFixed

object RendAnnounce {

	private var kuudraLastHP: Float = 25_000f
	private var inP4 = false

	fun init() {
		WorldChangeEvent.BEFORE.register { kuudraLastHP = 25_000f }

		ServerTickEvent.EVENT.register(::onTick)
	}

	private fun onTick() {
		MC.player?.let { inP4 = it.y <= 20 } ?: return
		KuudraUtils.kuudraEntity?.let {
			val diff = kuudraLastHP - it.health
			if(diff > 1666) {
				ChatUtils.sendMessage("Someone pulled for: ${diff.toFixed(2)} dmg")
			}

			kuudraLastHP = it.health
		}
	}
}