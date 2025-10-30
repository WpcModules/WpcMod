package net.wapic.wpcmod.features.dungeons.funnymap.ui

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.*
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.DungeonScan
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap
import net.wapic.wpcmod.features.dungeons.funnymap.ui.MapRenderer.darken
import net.wapic.wpcmod.features.dungeons.funnymap.ui.MapRenderer.grayScale
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils.CONNECTOR_SIZE
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils.halfRoomSize
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils.roomSize
import net.wapic.wpcmod.hud.SimpleHudElement
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.fillWithOutline
import java.awt.Color

object MapElement : SimpleHudElement("Dungeon Map", 128, 128) {

	private val legitPeekBind: KeyBinding = KeyBindingHelper.registerKeyBinding(KeyBinding("Legit Peek", InputUtil.GLFW_KEY_J, WpcMod.category))
	private val config get() = WpcMod.config.dungeon.funnyMap
	override val isEnabled: Boolean get() = config.mapEnabled

	val legitRender: Boolean get() = config.legitMode && !legitPeekBind.isPressed

	override fun render(drawContext: DrawContext, deltaTicks: Float) {
		if (!isActive()) return
		val player = MC.player ?: return
		val matrixStack = drawContext.matrices

		matrixStack.pushMatrix()
		applyTransformations(matrixStack)

		drawContext.fillWithOutline(
			0, 0,
			width, height,
			config.colors.mapBackground.getEffectiveColourRGB(),
			config.colors.mapBorder.getEffectiveColourRGB()
		)

		if (config.mapRotate) {
			matrixStack.pushMatrix()
			drawContext.enableScissor(0, 0, width, height)

			matrixStack.translate(64f, 64f)
			matrixStack.rotate(Math.toRadians(-player.yaw + 180.0).toFloat())

			if(config.mapCenter) {
				matrixStack.translate(
					-((player.x - DungeonScan.START_X + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.first - 2).toFloat(),
					-((player.z - DungeonScan.START_Z + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.second - 2).toFloat(),
				)
			} else {
				matrixStack.translate(-64f, -64f)
			}
		}

		renderRooms(drawContext)
		renderText(drawContext)
		renderPlayerHeads(drawContext)

		if (config.mapRotate) {
			drawContext.disableScissor()
			matrixStack.popMatrix()
		}

		matrixStack.popMatrix()
	}

	private fun renderRooms(drawContext: DrawContext) {
		val matrixStack = drawContext.matrices
		matrixStack.pushMatrix()
		matrixStack.translate(MapUtils.startCorner.first.toFloat(), MapUtils.startCorner.second.toFloat())

		var yPos = 0
		var yStep = 0

		for (y in 0..10) {
			val yEven = y % 2 == 0
			yPos += yStep
			yStep = if (yEven) roomSize else CONNECTOR_SIZE
			var xPos = 0
			var xStep = 0
			for (x in 0..10) {
				val xEven = x % 2 == 0
				xPos += xStep
				xStep = if (xEven) roomSize else CONNECTOR_SIZE

				val tile = FunnyMap.Info.dungeonList[y * 11 + x]
				if (tile is Unknown) continue
				if (legitRender && tile.state == RoomState.UNDISCOVERED) continue

				var color = tile.color

				if (tile.state.equalsOneOf(RoomState.UNDISCOVERED, RoomState.UNOPENED) &&
					!legitRender && FunnyMap.Info.startTime != 0L
				) {
					if (config.mapDarkenUndiscovered) {
						color = color.darken(1 - config.mapDarkenPercent)
					}
					if (config.mapGrayUndiscovered) {
						color = color.grayScale()
					}
				}

				when (tile) {
					is Room -> {
						drawContext.fill(xPos, yPos, xPos + xStep, yPos + yStep, color.rgb)
					}

					is Door -> {
						val doorOffset = if (roomSize == 16) 5 else 6
						if (xEven) {
							drawContext.fill(xPos + doorOffset, yPos, xPos + xStep - doorOffset, yPos + yStep, color.rgb)
						} else {
							drawContext.fill(xPos, yPos + doorOffset, xPos + xStep, yPos + yStep - doorOffset, color.rgb)
						}
					}
				}
			}
		}
		matrixStack.translate(-MapUtils.startCorner.first.toFloat(), -MapUtils.startCorner.second.toFloat())
		matrixStack.popMatrix()
	}

	private fun renderText(drawContext: DrawContext) {
		val matrixStack = drawContext.matrices
		matrixStack.pushMatrix()
		matrixStack.translate(MapUtils.startCorner.first.toFloat(), MapUtils.startCorner.second.toFloat())

		FunnyMap.Info.uniqueRooms.forEach { unique ->
			val room = unique.mainRoom
			if (legitRender && room.state.equalsOneOf(RoomState.UNDISCOVERED, RoomState.UNOPENED)) return@forEach
			val checkPos = unique.getCheckmarkPosition()
			val namePos = unique.getNamePosition()
			val xOffsetCheck = (checkPos.first / 2f) * (roomSize + CONNECTOR_SIZE)
			val yOffsetCheck = (checkPos.second / 2f) * (roomSize + CONNECTOR_SIZE)
			val xOffsetName = (namePos.first / 2f) * (roomSize + CONNECTOR_SIZE)
			val yOffsetName = (namePos.second / 2f) * (roomSize + CONNECTOR_SIZE)

			val name = mutableListOf<String>()

			if (config.mapRoomNames && room.data.type.equalsOneOf(
					RoomType.PUZZLE,
					RoomType.TRAP,
					RoomType.NORMAL,
					RoomType.RARE,
					RoomType.CHAMPION
				)
			)
			name.addAll(room.data.name.split(" "))

			// Offset + half of roomsize
			MapRenderer.renderCenteredText(
				drawContext,
				name,
				xOffsetName.toInt() + halfRoomSize,
				yOffsetName.toInt() + halfRoomSize,
				Color.white.rgb
			)

			if (config.mapCheckmark) {
				MapRenderer.drawCheckmark(drawContext, xOffsetCheck, yOffsetCheck, room.state)
			}
		}
		matrixStack.popMatrix()
	}

	fun renderPlayerHeads(drawContext: DrawContext) {
		try {
			if (FunnyMap.dungeonTeammates.isEmpty()) {
				MC.player?.let {
					MapRenderer.drawPlayerHead(drawContext, it.name.string, DungeonPlayer(it.skin).apply {
						yaw = it.yaw
					})
				}
			} else {
				FunnyMap.dungeonTeammates.forEach { (name, teammate) ->
					if (!teammate.dead) {
						MapRenderer.drawPlayerHead(drawContext, name, teammate)
					}
				}
			}
		} catch (_: ConcurrentModificationException) {
		}
	}

	fun isActive(): Boolean {
		if (!isEnabled || !DungeonUtils.inDungeons) return false
		if (DungeonUtils.bossSpawned && config.mapHideInBoss) return false
		return true
	}

}
