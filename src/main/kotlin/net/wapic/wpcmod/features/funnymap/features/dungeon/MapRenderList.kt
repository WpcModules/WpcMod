package net.wapic.wpcmod.features.funnymap.features.dungeon

import net.minecraft.client.gui.DrawContext
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.funnymap.core.map.*
import net.wapic.wpcmod.features.funnymap.features.dungeon.MapRender.legitRender
import net.wapic.wpcmod.features.funnymap.utils.MapUtils
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.connectorSize
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.halfRoomSize
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.roomSize
import net.wapic.wpcmod.features.funnymap.utils.RenderUtilsGL
import net.wapic.wpcmod.features.funnymap.utils.Utils.equalsOneOf
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.render.RenderUtils2D.darken
import net.wapic.wpcmod.util.render.RenderUtils2D.grayScale
import org.lwjgl.opengl.GL11

object MapRenderList {
	val config get() = WpcMod.config.funnyMap
	var renderUpdated = false
	private var borderGlList = -1
	private var roomGlList = -1

	fun updateRenderMap(drawContext: DrawContext) {
		drawContext.fill(0, 0, 128, 128, config.mapBackground.getEffectiveColourRGB())
		drawContext.drawBorder(0, 0, 128, 128, config.mapBorder.getEffectiveColourRGB())

		if (renderUpdated && config.renderBeta) {
			renderUpdated = false

			renderRooms(drawContext)
			renderText(drawContext)

		}
	}

	fun renderMap(drawContext: DrawContext) {
		val matrixStack = drawContext.matrices

		if (roomGlList == -1 || borderGlList == -1 || renderUpdated) {
			updateRenderMap(drawContext)
		}

		matrixStack.push()


		if (config.mapRotate) {
			matrixStack.push()
			MapRender.setupRotate(drawContext, matrixStack)
		}

		if (roomGlList != -1) GL11.glCallList(roomGlList)

		//RenderUtilsGL.unbindTexture()
		matrixStack.pop()

		if (!DungeonUtils.isBossSpawned()) {
			MapRender.renderPlayerHeads(drawContext)
		}

		if (config.mapRotate) {
			drawContext.disableScissor()
			matrixStack.pop()
		}
	}

	private fun renderRooms(drawContext: DrawContext) {
		val matrixStack = drawContext.matrices
		matrixStack.translate(MapUtils.startCorner.first.toFloat(), MapUtils.startCorner.second.toFloat(), 0f)

		var yPos = 0
		var yStep = 0

		for (y in 0..10) {
			val yEven = y % 2 == 0
			yPos += yStep
			yStep = if (yEven) roomSize else connectorSize
			var xPos = 0
			var xStep = 0
			for (x in 0..10) {
				val xEven = x % 2 == 0
				xPos += xStep
				xStep = if (xEven) roomSize else connectorSize

				val tile = Dungeon.Info.dungeonList[y * 11 + x]
				if (tile is Unknown) continue
				if (legitRender && tile.state == RoomState.UNDISCOVERED) continue

				var color = tile.color

				if (tile.state.equalsOneOf(RoomState.UNDISCOVERED, RoomState.UNOPENED) &&
					!legitRender && Dungeon.Info.startTime != 0L
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
						RenderUtilsGL.renderRect(drawContext, xPos, yPos, xStep, yStep, color)
						if (legitRender && tile.state == RoomState.UNOPENED) {
							RenderUtilsGL.drawCheckmark(drawContext, xPos.toFloat(), yPos.toFloat(), tile.state)
						}
					}

					is Door -> {
						val doorOffset = if (roomSize == 16) 5 else 6
						if (xEven) {
							RenderUtilsGL.renderRect(
								drawContext,
								xPos + doorOffset,
								yPos,
								xStep - doorOffset * 2,
								yStep,
								color
							)
						} else {
							RenderUtilsGL.renderRect(
								drawContext,
								xPos,
								yPos + doorOffset,
								xStep,
								yStep - doorOffset * 2,
								color
							)
						}
					}
				}
			}
		}
		matrixStack.translate(-MapUtils.startCorner.first.toFloat(), -MapUtils.startCorner.second.toFloat(), 0f)
	}

	private fun renderText(drawContext: DrawContext) {
		val matrixStack = drawContext.matrices
		matrixStack.translate(MapUtils.startCorner.first.toFloat(), MapUtils.startCorner.second.toFloat(), 0f)

		Dungeon.Info.uniqueRooms.forEach { unique ->
			val room = unique.mainRoom
			if (legitRender && room.state.equalsOneOf(RoomState.UNDISCOVERED, RoomState.UNOPENED)) return@forEach
			val checkPos = unique.getCheckmarkPosition()
			val namePos = unique.getNamePosition()
			val xPosCheck = (checkPos.first / 2f) * (roomSize + connectorSize)
			val yPosCheck = (checkPos.second / 2f) * (roomSize + connectorSize)
			val xPosName = (namePos.first / 2f) * (roomSize + connectorSize)
			val yPosName = (namePos.second / 2f) * (roomSize + connectorSize)

			if (config.mapCheckmark && config.mapRoomSecrets) {
				RenderUtilsGL.drawCheckmark(drawContext, xPosCheck, yPosCheck, room.state)
			}

			val color = if (config.mapColorText) when (room.state) {
				RoomState.GREEN -> config.colorTextGreen
				RoomState.CLEARED -> config.colorTextCleared
				RoomState.FAILED -> config.colorTextFailed
				else -> config.colorTextUncleared
			} else config.colorTextCleared

			if (config.mapRoomSecrets) {
				matrixStack.push()
				matrixStack.translate(
					xPosCheck + halfRoomSize, yPosCheck + 2 + halfRoomSize, 0f
				)
				matrixStack.scale(2f, 2f, 1f)
				RenderUtilsGL.renderCenteredText(
					drawContext,
					listOf(room.data.secrets.toString()),
					0,
					0,
					color.getEffectiveColour()
				)
				matrixStack.pop()
			}

			val name = mutableListOf<String>()

			if (config.mapRoomNames && room.data.type.equalsOneOf(
					RoomType.PUZZLE,
					RoomType.TRAP,
					RoomType.NORMAL,
					RoomType.RARE,
					RoomType.CHAMPION
				)
			) {
				name.addAll(room.data.name.split(" "))
			}
			if (room.data.type == RoomType.NORMAL && config.mapRoomSecrets) {
				name.add(room.data.secrets.toString())
			}
			// Offset + half of roomsize
			RenderUtilsGL.renderCenteredText(
				drawContext,
				name,
				xPosName.toInt() + halfRoomSize,
				yPosName.toInt() + halfRoomSize,
				color.getEffectiveColour()
			)
		}
		matrixStack.translate(-MapUtils.startCorner.first.toFloat(), -MapUtils.startCorner.second.toFloat(), 0f)
	}
}
