package net.wapic.wpcmod.features.kuudra

import net.wapic.wpcmod.events.ServerTickEvent
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.KuudraUtils
import java.text.DecimalFormat

object RendAnnounce {

	private var kuudraLastHP: Float = 25_000f

	fun init() {
		WorldChangeEvent.BEFORE.register { kuudraLastHP = 25_000f }

		ServerTickEvent.EVENT.register(::onTick)
	}

	fun format(value: Float): String = DecimalFormat("#,###").format(value)

	private fun onTick() {
		if(KuudraUtils.phase != KuudraUtils.Phase.KILL) return
		KuudraUtils.kuudraEntity?.let {
			val diff = kuudraLastHP - it.health
			if(diff > 1666) {
				ChatUtils.sendMessage("Someone pulled for: ${format(diff * 9600f)} damage")
			}

			kuudraLastHP = it.health
		}
	}
}