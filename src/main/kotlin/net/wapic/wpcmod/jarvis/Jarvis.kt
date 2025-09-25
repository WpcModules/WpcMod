package net.wapic.wpcmod.jarvis

import moe.nea.jarvis.api.JarvisHud
import moe.nea.jarvis.api.JarvisPlugin
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.ScoreCalculation
import org.jetbrains.annotations.Unmodifiable

class Jarvis : JarvisPlugin {

	private val hudElements = listOf<JarvisHud>(
		ScoreCalculation,
	)

	override fun getModId(): String {
		return WpcMod.MOD_ID
	}

	override fun getAllHuds(): @Unmodifiable List<JarvisHud> {
		return hudElements
	}
}