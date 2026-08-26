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
import net.wapic.wpcmod.util.SafariAPI.SafariBiome.Companion.isSimilarTo

object SafariTracker : SimpleHudElement("Safari Tracker", 90, 408) {

	private val config get() = WpcMod.config.hunting.safari.tracker

	// ^§a§lCAPTURE! §7You caught a §6§lSPARKLING §.(?<entityName>.+)§7 and received (?:a|\\dx) §5Rainbow Feather§7 and \\d+x §.(?<entityName>.+) Shard§7!$
	// ^§e§lLOOT SHARE! §7You received (?:a|\\dx) §5Rainbow Feathers?§7 and \\d+x §.(?<entityName>.+) Shard§7 from §.+§7 catching a §6§lSPARKLING §.+§7!$
	// ^SPARKLING! (?<user>.+) caught a SPARKLING (?<entityName>.+)!$
	private val captureRegexes = listOf(
		Regex("^§a§lCAPTURE! §7You (?:found|caught) (?:an?|the) §.(?<entityName>.+)§7,? and (?:as a reward it gave you|gained) (?:an?|\\dx) §.+ Shard§7!$"),
		Regex("^§e§lLOOT SHARE! §7You received (?:an?|\\dx) §.+ Shard§7 from §.+§7 (?:catching an?|finding the) §.(?<entityName>.+)§7!$"),
		Regex("^SPARKLING! .+ caught a SPARKLING (?<entityName>.+)!$"),
	)
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

		val matchedRegex = captureRegexes.firstNotNullOfOrNull { it.matchEntire(text) } ?: return
		val entityName = matchedRegex.groups["entityName"]?.value ?: return
		val capturedCritter = SafariAPI.Critter.fromName(entityName) ?: return

		tracker[capturedCritter] = (tracker[capturedCritter] ?: 0) + 1
	}

	override fun render(drawContext: GuiGraphicsExtractor, deltaTicks: Float) {
		if (!isEnabled || !isActive) return
		val player = MC.player ?: return

		val tracker =
			if (config.onlyCurrentBiome) tracker.filterKeys { it.biome.isSimilarTo(player.biome) } else tracker

		val matrixStack = drawContext.pose()
		matrixStack.pushMatrix()
		applyTransformations(matrixStack)

		var y = 1
		for ((critter, count) in tracker) {
			val color = critter.biome.color.char
			drawContext.text(MC.font, "§$color${critter.entityName}§r: $count", 2, y, CommonColors.WHITE, true)
			y += 11
		}

		matrixStack.popMatrix()
	}

	override val isActive: Boolean get() = SafariAPI.inSafari
	override val isEnabled: Boolean get() = config.showTracker
}