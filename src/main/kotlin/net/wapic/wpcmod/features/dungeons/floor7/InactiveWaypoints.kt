package net.wapic.wpcmod.features.dungeons.floor7

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.math.Box
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.jarvis.SimpleHudElement
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.drawBeaconBeam
import net.wapic.wpcmod.util.render.drawFilledBoxWithOutline
import net.wapic.wpcmod.util.render.drawText

object InactiveWaypoints : SimpleHudElement("Term Info", w = 60, h = 30) {

	private val config get() = WpcMod.config.dungeon.floor7.inactiveWaypoints

    private var inactiveList = setOf<ArmorStandEntity>()
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
		WorldRenderEvents.END.register(::onRenderWorld)

		ClientTickEvents.END_WORLD_TICK.register {
			if (DungeonUtils.getF7Phase() != DungeonUtils.F7Phase.GOLDOR || !config.enabled) return@register
			inactiveList = it.entities.filterIsInstance<ArmorStandEntity>().filter { entity ->
				entity.name.string.equalsOneOf("Inactive", "Not Activated", "CLICK HERE")
			}.toSet()
		}
    }

	override fun render(drawContext: DrawContext, renderTickCounter: RenderTickCounter) {
		if (!DungeonUtils.bossSpawned || !shouldRender || !config.enabled) return
		val matrixStack = drawContext.matrices
		matrixStack.push()
		applyTransformations(matrixStack)

		val lines = setOf(
			"§6Terms ${if ((section == 2 && terminals == 5) || (section != 2 && terminals == 4)) "§a" else "§c"}${terminals}§8/§a${if (section == 2) 5 else 4}",
			"§6Device ${if (device) "§a✔" else "§c✘"}",
			"§6Gate ${if (gate) "§a✔" else "§c✘"}"
		)

		for ((index, line) in lines.withIndex()) {
			drawContext.drawText(
				line,
				1, 2 + MC.textRenderer.fontHeight * index,
				Colors.WHITE,
				true
			)
		}

		matrixStack.pop()
	}

    fun onMessageReceived(text: Text, actionBar: Boolean) {
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

    fun onWorldLoad(world: ClientWorld) {
        shouldRender = false
        resetState()
    }

    private fun resetState() {
        inactiveList = emptySet()
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

    fun onRenderWorld(worldRenderContext: WorldRenderContext) {
        if (inactiveList.isEmpty() || DungeonUtils.getF7Phase() != DungeonUtils.F7Phase.GOLDOR || !config.enabled) return
        inactiveList.forEach {
            val name = it.name.string
            if ((name == "Inactive Terminal" && config.showTerminals) || (name == "Inactive" && config.showDevices) || (name == "Not Activated" && config.showLevers)) {
                val customName = Text.of(if (name == "Inactive Terminal") "Terminal" else if (name == "Inactive") "Device" else "Lever").asOrderedText()
                if (config.renderBox)
					worldRenderContext.drawFilledBoxWithOutline(
						Box.from(it.pos.add(-0.5, 0.0, -0.5)),
						config.color.getEffectiveColour().darker(),
						config.color.getEffectiveColour().brighter()
					)
                if (config.renderText)
					worldRenderContext.drawText(customName, it.pos.add(0.0, 2.0, 0.0), 1.5f, true)
                if (config.renderBeacon)
					worldRenderContext.drawBeaconBeam(it.blockPos, config.color.getEffectiveColour())
            }
            it.isCustomNameVisible = !config.hideDefault
        }
    }
}