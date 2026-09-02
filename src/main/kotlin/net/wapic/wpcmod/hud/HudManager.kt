package net.wapic.wpcmod.hud

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.ScoreCalculation
import net.wapic.wpcmod.features.dungeons.SpiritBearTimer
import net.wapic.wpcmod.features.dungeons.floor7.InactiveWaypoints
import net.wapic.wpcmod.features.dungeons.floor7.InvincibilityTimer
import net.wapic.wpcmod.features.dungeons.floor7.TickTimers
import net.wapic.wpcmod.features.dungeons.funnymap.ui.MapElement
import net.wapic.wpcmod.features.hunting.SafariTracker
import net.wapic.wpcmod.features.kuudra.KuudraDisplay
import net.wapic.wpcmod.features.slayer.GummyBearTimer
import net.wapic.wpcmod.util.FileManager
import net.wapic.wpcmod.util.MC
import java.io.File

object HudManager {

	private val file = File(WpcMod.configDir, "hud-locations.json")
	private val backupFile = File(file.parentFile, "${file.name}.bak")
	private val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()
	private val config get() = WpcMod.config

	private val hudKeyBind: KeyMapping = KeyMappingHelper.registerKeyMapping(
		KeyMapping(
			"Edit Hud",
			InputConstants.KEY_END,
			WpcMod.category
		)
	)

	private val hudElements = listOf(
		ScoreCalculation,
		KuudraDisplay,
		TickTimers,
		InactiveWaypoints,
		MapElement,
		SpiritBearTimer,
		InvincibilityTimer,
		GummyBearTimer,
		SafariTracker,
	)

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register {
			while (hudKeyBind.consumeClick()) {
				openEditor()
			}
		}

		loadLocations()
	}

	fun openEditor() {
		MC.instance.schedule { MC.screen = HudEditor(hudElements) }
	}

	fun resetLocations() {
		hudElements.forEach {
			it.x = it.defaultX
			it.y = it.defaultY
			it.scale = it.defaultScale
		}
	}

	fun loadLocations() {
		val loadedElements = FileManager.loadFile<Array<SimpleHudElement>>(file, backupFile)?.toList()

		loadedElements?.forEach {
			WpcMod.LOGGER.debug("Applying location of: ${it.label}")
			val element = hudElements.find { element -> element.label == it.label }
			if (it.width > 0) element?.width = it.width
			if (it.height > 0) element?.height = it.height
			element?.scale = it.scale
			element?.x = it.x
			element?.y = it.y
		} ?: return WpcMod.LOGGER.error("An error occurred while applying hud locations!")
	}

	fun saveLocations() {
		val json = gson.toJson(hudElements)
		FileManager.saveFile(json, file, backupFile)
	}
}