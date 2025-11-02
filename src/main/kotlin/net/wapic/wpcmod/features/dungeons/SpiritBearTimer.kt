package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Colors
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
import net.wapic.wpcmod.util.Utils.toFixed
import net.wapic.wpcmod.util.render.drawText

object SpiritBearTimer : SimpleHudElement("Spirit Bear Timer", 90, 12) {

	private val config get() = WpcMod.config.dungeon
	override val isEnabled: Boolean get() = config.spiritBear

	private const val SPAWN_TIME_IN_TICKS: Int = 68 // Ticks

	private val lastBlockPos = BlockPos(7, 77, 34)
	private val isThornFloor get() = currentFloor.equalsOneOf(DungeonFloor.FLOOR_4, DungeonFloor.MASTER_MODE_FLOOR_4)

	private var spawnTime: Int = 0

	fun init() {
		BlockEvents.CHANGE.register(::onBlockChange)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		ServerTickEvent.EVENT.register {
			if (spawnTime > 0 && isActive()) spawnTime--
		}
		WorldChangeEvent.BEFORE.register {
			spawnTime = 0
		}
	}

	override fun render(drawContext: DrawContext, deltaTicks: Float) {
		if (!isActive() || spawnTime <= 0) return
		val matrixStack = drawContext.matrices
		matrixStack.pushMatrix()
		applyTransformations(matrixStack)

		drawContext.drawText(
			"Spirit Bear: ${(spawnTime / 20f).toFixed()}s",
			x = 2, y = 2,
			Colors.LIGHT_PINK,
			shadow = true
		)

		matrixStack.popMatrix()
	}

	fun onBlockChange(pos: BlockPos, oldState: BlockState, newState: BlockState) {
		if (!isActive()) return

		if (pos == lastBlockPos && newState.block == Blocks.SEA_LANTERN) {
			spawnTime = SPAWN_TIME_IN_TICKS
		}
	}

	fun onMessageReceived(text: Text, actionBar: Boolean) {
		if (!isActive() || actionBar) return
		if (text.string == "A Spirit Bear has appeared!") spawnTime = 0
	}

	fun isActive(): Boolean {
		return isEnabled && isThornFloor && bossSpawned
	}
}