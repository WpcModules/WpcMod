package net.wapic.wpcmod.jarvis

import moe.nea.jarvis.api.Jarvis
import moe.nea.jarvis.api.JarvisHud
import moe.nea.jarvis.api.JarvisPlugin
import net.wapic.wpcmod.WpcMod
import org.jetbrains.annotations.Unmodifiable

class JarvisInitializer : JarvisPlugin {

	override fun getModId(): String {
		return WpcMod.MOD_ID
	}

	override fun getAllHuds(): @Unmodifiable List<JarvisHud> {
		return JarvisManager.hudElements
	}

	override fun onInitialize(jarvis: Jarvis) {
		JarvisManager.loadLocations()
		super.onInitialize(jarvis)
	}

	override fun onHudEditorClosed() {
		JarvisManager.saveLocations()
		super.onHudEditorClosed()
	}
}