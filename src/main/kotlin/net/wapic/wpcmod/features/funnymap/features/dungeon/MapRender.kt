package net.wapic.wpcmod.features.funnymap.features.dungeon

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.funnymap.FunnyMap.mc
import net.wapic.wpcmod.features.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.funnymap.core.map.*
import net.wapic.wpcmod.features.funnymap.utils.MapUtils
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.connectorSize
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.halfRoomSize
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.roomSize
import net.wapic.wpcmod.features.funnymap.utils.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.RenderUtils2D
import net.wapic.wpcmod.util.render.RenderUtils2D.darken
import net.wapic.wpcmod.util.render.RenderUtils2D.grayScale
import java.awt.Color

object MapRender {
	val config get() = WpcMod.config.funnyMap
	var dynamicRotation = 0f

	val legitRender: Boolean get() = config.legitMode

	fun renderMap(drawContext: DrawContext, matrixStack: MatrixStack) {
		drawContext.fill(0, 0, 128, 128, config.mapBackground.getEffectiveColourRGB())
		drawContext.drawBorder(0, 0, 128, 128, config.mapBorder.getEffectiveColourRGB())

		if (config.mapRotate) {
			setupRotate(drawContext, matrixStack)
		}

		renderRooms(drawContext, matrixStack)
		renderText(drawContext, matrixStack)
		renderPlayerHeads(drawContext)

		if (config.mapRotate) {
			drawContext.disableScissor()
		}
	}

	fun setupRotate(drawContext: DrawContext, matrixStack: MatrixStack) {
		val scale = mc.window.scaleFactor

		drawContext.enableScissor(
			((config.mapX * scale).toInt()),
			(mc.window.height - config.mapY * scale - 128 * scale * config.mapScale).toInt(),
			(128 * scale * config.mapScale).toInt(),
			(128 * scale * config.mapScale).toInt()
		)

		val player = mc.player ?: return

		matrixStack.translate(64.0, 64.0, 0.0)
		val rotation = RotationAxis.of(player.rotationVector.toVector3f()).rotation(player.yaw + 180f)
		matrixStack.multiply(rotation)

		if (config.mapCenter) {
			matrixStack.translate(
				-((player.pos.x - DungeonScan.START_X + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.first - 2),
				-((player.pos.z - DungeonScan.START_Z + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.second - 2),
				0.0
			)
		} else {
			matrixStack.translate(-64.0, -64.0, 0.0)
		}
	}

	private fun renderRooms(drawContext: DrawContext, matrixStack: MatrixStack) {
		matrixStack.translate(MapUtils.startCorner.first.toFloat(), MapUtils.startCorner.second.toFloat(), 0f)

		for (y in 0..10) {
			for (x in 0..10) {
				val tile = Dungeon.Info.dungeonList[y * 11 + x]
				if (tile is Unknown) continue
				if (legitRender && tile.state == RoomState.UNDISCOVERED) continue

				val xOffset = (x shr 1) * (roomSize + connectorSize)
				val yOffset = (y shr 1) * (roomSize + connectorSize)

				val xEven = x and 1 == 0
				val yEven = y and 1 == 0

				var color = tile.color

				if (tile.state.equalsOneOf(
						RoomState.UNDISCOVERED,
						RoomState.UNOPENED
					) && !legitRender && Dungeon.Info.startTime != 0L
				) {
					if (config.mapDarkenUndiscovered) {
						color = color.darken(1 - config.mapDarkenPercent)
					}
					if (config.mapGrayUndiscovered) {
						color = color.grayScale()
					}
				}

				when {
					xEven && yEven -> if (tile is Room) {
						RenderUtils2D.renderRect(
							drawContext,
							xOffset,
							yOffset,
							roomSize,
							roomSize,
							color
						)

						if (legitRender && tile.state == RoomState.UNOPENED) {
							RenderUtils2D.drawCheckmark(drawContext, xOffset.toFloat(), yOffset.toFloat(), tile.state)
						}
					}

					!xEven && !yEven -> {
						RenderUtils2D.renderRect(
							drawContext,
							xOffset,
							yOffset,
							(roomSize + connectorSize),
							(roomSize + connectorSize),
							color
						)
					}

					else -> drawRoomConnector(
						drawContext,
						xOffset, yOffset, connectorSize, tile is Door, !xEven, color
					)
				}
			}
		}
	}

	private fun renderText(drawContext: DrawContext, matrixStack: MatrixStack) {
		matrixStack.translate(MapUtils.startCorner.first.toFloat(), MapUtils.startCorner.second.toFloat(), 0f)

		Dungeon.Info.uniqueRooms.forEach { unique ->
			val room = unique.mainRoom
			if (legitRender && room.state.equalsOneOf(RoomState.UNDISCOVERED, RoomState.UNOPENED)) return@forEach
			val checkPos = unique.getCheckmarkPosition()
			val namePos = unique.getNamePosition()
			val xOffsetCheck = (checkPos.first / 2f) * (roomSize + connectorSize)
			val yOffsetCheck = (checkPos.second / 2f) * (roomSize + connectorSize)
			val xOffsetName = (namePos.first / 2f) * (roomSize + connectorSize)
			val yOffsetName = (namePos.second / 2f) * (roomSize + connectorSize)

			if (config.mapCheckmark && config.mapRoomSecrets) {
				RenderUtils2D.drawCheckmark(drawContext, xOffsetCheck, yOffsetCheck, room.state)
			}

			val color = (if (config.mapColorText) when (room.state) {
				RoomState.GREEN -> config.colorTextGreen
				RoomState.CLEARED -> config.colorTextCleared
				RoomState.FAILED -> config.colorTextFailed
				else -> config.colorTextUncleared
			} else config.colorTextCleared).getEffectiveColourRGB()

			if (config.mapRoomSecrets) {
				matrixStack.push()
				matrixStack.translate(
					xOffsetCheck + halfRoomSize, yOffsetCheck + 2 + halfRoomSize, 0f
				)
				matrixStack.scale(2f, 2f, 1f)
				RenderUtils2D.renderCenteredText(drawContext, listOf(room.data.secrets.toString()), 0, 0, color)
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
			RenderUtils2D.renderCenteredText(
				drawContext,
				name,
				xOffsetName.toInt() + halfRoomSize,
				yOffsetName.toInt() + halfRoomSize,
				color
			)
		}
	}

	fun renderPlayerHeads(drawContext: DrawContext) {
		try {
			if (Dungeon.dungeonTeammates.isEmpty()) {
				val player = mc.player ?: return
				RenderUtils2D.drawPlayerHead(drawContext, player.name.string, DungeonPlayer(player.skinTextures).apply {
					yaw = player.yaw
				})
			} else {
				Dungeon.dungeonTeammates.forEach { (name, teammate) ->
					if (!teammate.dead) {
						RenderUtils2D.drawPlayerHead(drawContext, name, teammate)
					}
				}
			}
		} catch (_: ConcurrentModificationException) {
		}
	}

	private fun drawRoomConnector(
		drawContext: DrawContext,
		x: Int,
		y: Int,
		doorWidth: Int,
		doorway: Boolean,
		vertical: Boolean,
		color: Color,
	) {
		val doorwayOffset = if (roomSize == 16) 5 else 6
		val width = if (doorway) 6 else roomSize
		var x1 = if (vertical) x + roomSize else x
		var y1 = if (vertical) y else y + roomSize
		if (doorway) {
			if (vertical) y1 += doorwayOffset else x1 += doorwayOffset
		}
		RenderUtils2D.renderRect(
			drawContext,
			x1,
			y1,
			if (vertical) doorWidth else width,
			if (vertical) width else doorWidth,
			color
		)
	}
}
