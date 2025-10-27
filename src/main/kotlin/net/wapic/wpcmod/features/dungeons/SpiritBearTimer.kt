package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.BlockEvents
import net.wapic.wpcmod.events.ServerTickEvent
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.hud.SimpleHudElement
import net.wapic.wpcmod.util.DungeonUtils.DungeonFloor
import net.wapic.wpcmod.util.DungeonUtils.bossSpawned
import net.wapic.wpcmod.util.DungeonUtils.currentFloor
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.drawText
import java.awt.Color

object SpiritBearTimer : SimpleHudElement("Spirit Bear Timer", 90, 12) {

	private val config get() = WpcMod.config.dungeon

	private const val SPAWN_TIME_IN_TICKS: Int = 68 // Ticks

	private val lastBlockPos = BlockPos(7, 77, 34)
	private val isThornFloor get() = currentFloor.equalsOneOf(DungeonFloor.FLOOR_4, DungeonFloor.MASTER_MODE_FLOOR_4)

	private var lastLitUpTime: Int = 0

	fun init() {
		BlockEvents.CHANGE.register(::onBlockChange)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		ServerTickEvent.EVENT.register {
			if (lastLitUpTime > 0 && isActive()) lastLitUpTime--
		}
		WorldChangeEvent.BEFORE.register {
			lastLitUpTime = 0
		}
	}

	override fun render(drawContext: DrawContext, deltaTicks: Float) {
		if (!isActive() || lastLitUpTime <= 0) return
		val matrixStack = drawContext.matrices
		matrixStack.pushMatrix()
		applyTransformations(matrixStack)

		drawContext.drawText(
			"Spirit Bear: ${String.format("%.2f", lastLitUpTime / 20f)}s",
			x = 2, y = 2,
			Color.MAGENTA.rgb,
			shadow = true
		)

		matrixStack.popMatrix()
	}

	fun onBlockChange(pos: BlockPos, oldState: BlockState, newState: BlockState) {
		if (!isActive()) return

		if (pos == lastBlockPos && newState.block == Blocks.SEA_LANTERN) {
			lastLitUpTime = SPAWN_TIME_IN_TICKS
		}
	}

	fun onMessageReceived(text: Text, actionBar: Boolean) {
		if (!isActive() || actionBar) return
		if (text.string == "A Spirit Bear has appeared!") lastLitUpTime = 0
	}

	fun isActive(): Boolean {
		return isEnabled() && isThornFloor && bossSpawned
	}

	fun isEnabled(): Boolean {
		return config.spiritBear
	}
}