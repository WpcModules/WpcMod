package net.wapic.wpcmod.features.hunting

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.hud.SimpleHudElement
import net.wapic.wpcmod.util.EntityUtils.biome
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.SafariAPI
import net.wapic.wpcmod.util.SafariAPI.SafariBiome.Companion.isOf

object SafariTracker : SimpleHudElement("Safari Tracker", 80, 408) {

	private val config get() = WpcMod.config.hunting.safari.tracker
	private val lootshareRegex =
		Regex("^§e§lLOOT SHARE! §7You received (?:an?|\\dx) §.* Shard§7 from §.*§7 catching an? §.(?<entityName>.+)§7!$")
	private val captureRegex =
		Regex("^§a§lCAPTURE! §7You (?:found|caught) (?:an?|the) §.(?<entityName>.+)§7,? and (?:as a reward it gave you|gained) (?:an?|\\dx) §.* Shard§7!$")
	private val tracker = mutableMapOf<SafariAPI.Critter, Int>()

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		WorldChangeEvent.AFTER.register {
			tracker.clear()
			SafariAPI.Critter.entries.associateWithTo(tracker) { 0 }
		}
	}

	private fun onMessageReceived(message: Component, actionBar: Boolean) {
		if (actionBar && !isActive) return
		val text = message.string
		val entityName = lootshareRegex.matchEntire(text)?.groups["entityName"]?.value
			?: captureRegex.matchEntire(text)?.groups["entityName"]?.value ?: return
		val capturedCritter = SafariAPI.Critter.entries.find { it.entityName == entityName } ?: return

		tracker[capturedCritter] = (tracker[capturedCritter] ?: 0) + 1
	}

	override fun render(drawContext: GuiGraphicsExtractor, deltaTicks: Float) {
		if (!isEnabled || !isActive) return
		val player = MC.player ?: return

		val shownTracker = if (config.onlyCurrentBiome) tracker.filterKeys { player.biome.isOf(it.biome) } else tracker

		val matrixStack = drawContext.pose()
		matrixStack.pushMatrix()
		applyTransformations(matrixStack)

		var y = 0
		for ((critter, count) in shownTracker) {
			val color = critter.biome.color.char
			drawContext.text(MC.font, "§$color${critter.entityName}§r: $count", 2, y, CommonColors.WHITE, true)
			y += 11
		}

		matrixStack.popMatrix()
	}

	override val isActive: Boolean get() = SafariAPI.inSafari
	override val isEnabled: Boolean get() = config.showTracker
}