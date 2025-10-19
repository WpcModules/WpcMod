package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.text.Text
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.BlockEvents
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.jarvis.SimpleHudElement
import net.wapic.wpcmod.util.DungeonUtils.DungeonFloor
import net.wapic.wpcmod.util.DungeonUtils.bossSpawned
import net.wapic.wpcmod.util.DungeonUtils.currentFloor
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.drawText
import java.awt.Color

object SpiritBearTimer : SimpleHudElement("Spirit Bear Timer", 80, 11) {

	private val config get() = WpcMod.config.dungeon

	private val lastBlockPos = BlockPos(7, 77, 34)
	private val isThornFloor get() = currentFloor.equalsOneOf(DungeonFloor.FLOOR_4, DungeonFloor.MASTER_MODE_FLOOR_4)
	private val spawnTime: Long get() = lastLitUpTime + 3400L

	private var lastLitUpTime: Long = 0L

	fun init() {
		BlockEvents.CHANGE.register(::onBlockChange)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		WorldChangeEvent.BEFORE.register {
			lastLitUpTime = 0L
		}
	}

	override fun render(drawContext: DrawContext, renderTickCounter: RenderTickCounter) {
		if (!isActive || lastLitUpTime == 0L) return
		val matrixStack = drawContext.matrices
		matrixStack.push()
		applyTransformations(matrixStack)

		drawContext.drawText(
			"Spirit Bear: ${(spawnTime - Util.getMeasuringTimeMs()) / 1000}s",
			x = 0, y = 0,
			Color.MAGENTA.rgb,
			shadow = true
		)

		matrixStack.pop()
	}

	fun onBlockChange(pos: BlockPos, oldState: BlockState, newState: BlockState) {
		if (!isActive) return

		if (pos == lastBlockPos && newState.block == Blocks.SEA_LANTERN) {
			lastLitUpTime = Util.getMeasuringTimeMs()
		}
	}

	fun onMessageReceived(text: Text, actionBar: Boolean) {
		if (actionBar) return

		if (text.string == "A Spirit Bear has appeared!") {
			lastLitUpTime = 0L
		}
	}

	override fun isActive(): Boolean {
		return isEnabled && isThornFloor && bossSpawned
	}

	override fun isEnabled(): Boolean {
		return config.spiritBear
	}
}