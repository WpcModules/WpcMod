package net.wapic.wpcmod.features.funnymap.utils

import net.wapic.wpcmod.features.funnymap.FunnyMap.mc

object Utils {
	fun Any?.equalsOneOf(vararg other: Any): Boolean = other.any { this == it }

	fun runMinecraftThread(run: () -> Unit) {
		if (!mc.isOnThread) {
			mc.execute { run }
		} else run()
	}
}
