package net.wapic.wpcmod.features.dungeons.floor7

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.decoration.ArmorStand
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.hud.SimpleHudElement
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.dungeons.DungeonUtils
import net.wapic.wpcmod.util.render.WHITE
import net.wapic.wpcmod.util.render.WpcModExtractionContext
import net.wapic.wpcmod.util.render.darker

object InactiveWaypoints : SimpleHudElement("Term Info", 60, 30) {

	private val config get() = WpcMod.config.dungeon.floor7.inactiveWaypoints
	override val isEnabled: Boolean get() = config.enabled
	override val isActive: Boolean get() = isEnabled && shouldRender

	private var firstInSection = false
	private var shouldRender = false
	private var isComplete = false
	private var lastCompleted = 0
	private var device = false
	private var terminals = 0
	private var gate = false
	private var section = 1
	private var levers = 0

	private val completedRegex = Regex("^(.{1,16}) (activated|completed) a (terminal|lever|device)! \\((\\d)/(\\d)\\)$")
	private val goldorRegex = Regex("^\\[BOSS] Goldor: Who dares trespass into my domain\\?$")
	private val coreOpeningRegex = Regex("^The Core entrance is opening!$")
	private val gateRegex = Regex("^The gate has been destroyed!$")

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		WorldChangeEvent.BEFORE.register(::onWorldLoad)
		WorldRenderEvent.EVENT.register(::onRenderWorld)
	}

	override fun render(drawContext: GuiGraphicsExtractor, deltaTicks: Float) {
		if (!isActive) return
		val matrixStack = drawContext.pose()
		matrixStack.pushMatrix()
		applyTransformations(matrixStack)

		val lines = setOf(
			"§6Terms ${if ((section == 2 && terminals == 5) || (section != 2 && terminals == 4)) "§a" else "§c"}${terminals}§8/§a${if (section == 2) 5 else 4}",
			"§6Device ${if (device) "§a✔" else "§c✘"}",
			"§6Gate ${if (gate) "§a✔" else "§c✘"}"
		)

		for ((index, line) in lines.withIndex()) {
			drawContext.text(
				MC.font,
				line,
				1, 2 + MC.font.lineHeight * index,
				CommonColors.WHITE,
				true
			)
		}

		matrixStack.popMatrix()
	}

	fun onMessageReceived(text: Component, actionBar: Boolean) {
		if (actionBar || !DungeonUtils.bossSpawned || !config.enabled) return
		val text = text.string

		when {
			completedRegex.matches(text) -> {
				val it = completedRegex.find(text) ?: return
				val completed = (it.groupValues[4].toIntOrNull() ?: 0).apply { if (this == 1) firstInSection = true }

				if (completed == (it.groupValues[5].toIntOrNull() ?: 0)) {
					if (gate) newSection() else isComplete = true
					return
				}

				when (it.groupValues[3]) {
					"lever" -> levers++
					"terminal" -> terminals++
					"device" -> if (!firstInSection || lastCompleted != completed) device = true
				}
				lastCompleted = completed
			}

			gateRegex.matches(text) -> {
				gate = true
				if (isComplete) newSection()
			}

			goldorRegex.matches(text) -> {
				shouldRender = true
				resetState()
				section = 1
			}

			coreOpeningRegex.matches(text) -> {
				shouldRender = false
				resetState()
			}
		}
	}

	fun onWorldLoad(world: ClientLevel) {
		shouldRender = false
		resetState()
	}

	private fun resetState() {
		firstInSection = false
		lastCompleted = 0
		isComplete = false
		device = false
		terminals = 0
		gate = false
		section = 1
		levers = 0
	}

	private fun newSection() {
		firstInSection = false
		isComplete = false
		device = false
		terminals = 0
		gate = false
		levers = 0
		section++
	}

	fun onRenderWorld(context: WpcModExtractionContext, profiler: ProfilerFiller) {
		if (DungeonUtils.getF7Phase() != DungeonUtils.F7Phase.GOLDOR || !config.enabled) return
		profiler.push("InactiveWaypoints")
		context.level.entitiesForRendering().filterIsInstance<ArmorStand>().forEach {
			if (it.name.string == "CLICK HERE") {
				it.isCustomNameVisible = !(config.showTerminals && config.hideDefault)
				return@forEach
			}

			val (customName, yOffset, isEnabled) = when (it.name.string) {
				"Inactive Terminal" -> Triple("Terminal", 1.0, config.showTerminals)
				"Inactive" -> Triple("Device", 1.5, config.showDevices)
				"Not Activated" -> Triple("Lever", 2.0, config.showLevers)
				else -> return@forEach
			}

			// set visibility before returning in case players decide to re-enable default nametags mid-run
			it.isCustomNameVisible = !(config.hideDefault && isEnabled)
			if (!isEnabled) return@forEach

			val pos = it.position().add(-0.5, yOffset, -0.5)
			if (config.renderBox) context.filledBox(pos, 1f, 1f, config.color.darker(), config.color)
			if (config.renderText) context.text(customName, pos, ChromaColour.WHITE, 2f, shadow = true, background = true)
		}
		profiler.pop()
	}
}