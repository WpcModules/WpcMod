package net.wapic.wpcmod.features.funnymap.dungeon

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.funnymap.core.map.*
import net.wapic.wpcmod.features.funnymap.utils.MapUtils
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.CONNECTOR_SIZE
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.halfRoomSize
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.roomSize
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.RenderUtils2D
import net.wapic.wpcmod.util.render.RenderUtils2D.darken
import net.wapic.wpcmod.util.render.RenderUtils2D.grayScale
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.render.RenderUtils2D.axis
import net.wapic.wpcmod.util.render.drawText
import java.awt.Color

object MapRender {
	val config get() = WpcMod.config.funnyMap
	var dynamicRotation = 0f

	val legitRender: Boolean get() = config.legitMode

	fun renderMap(drawContext: DrawContext, width: Int, height: Int) {
		val matrixStack = drawContext.matrices
		drawContext.fill(0, 0, width, height, config.mapBackground.getEffectiveColourRGB())
		drawContext.drawBorder(0, 0, width, height, config.mapBorder.getEffectiveColourRGB())

		if (config.mapRotate) {
			matrixStack.push()
			drawContext.enableScissor(0, 0, width, height)
			setupRotate(matrixStack)
		}

		renderRooms(drawContext)
		renderText(drawContext)
		renderPlayerHeads(drawContext)

		if (config.mapRotate) {
			drawContext.disableScissor()
			matrixStack.pop()
		}
	}

	fun setupRotate(matrixStack: MatrixStack) {
		val player = MC.player ?: return

		matrixStack.translate(64.0, 64.0, 0.0)
		matrixStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(player.yaw + 180f))

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

	private fun renderRooms(drawContext: DrawContext) {
		val matrixStack = drawContext.matrices
		matrixStack.push()
		matrixStack.translate(MapUtils.startCorner.first.toFloat(), MapUtils.startCorner.second.toFloat(), 0f)

		for (y in 0..10) {
			for (x in 0..10) {
				val tile = Dungeon.Info.dungeonList[y * 11 + x]
				if (tile is Unknown) continue
				if (legitRender && tile.state == RoomState.UNDISCOVERED) continue

				val xOffset = (x shr 1) * (roomSize + CONNECTOR_SIZE)
				val yOffset = (y shr 1) * (roomSize + CONNECTOR_SIZE)

				val xEven = x and 1 == 0
				val yEven = y and 1 == 0

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

				when {
					xEven && yEven -> if (tile is Room) {
						RenderUtils2D.renderRect(
							drawContext,
							xOffset, yOffset,
							roomSize, roomSize,
							color.rgb
						)

						if(legitRender && tile.state == RoomState.UNOPENED) {
							RenderUtils2D.drawCheckmark(drawContext, xOffset.toFloat(), yOffset.toFloat(), tile.state)
						}
					}

					!xEven && !yEven -> {
						continue
						RenderUtils2D.renderRect(
							drawContext,
							xOffset, yOffset,
							roomSize ,
							roomSize ,
							color.rgb
						)
					}

					else -> {
						drawRoomConnector(
							drawContext,
							xOffset, yOffset,
							tile,
							!xEven,
							color
						)
					}
				}
			}
		}
		matrixStack.pop()
	}

	private fun renderText(drawContext: DrawContext) {
		val matrixStack = drawContext.matrices
		val yaw = MC.player?.yaw ?: return
		matrixStack.push()
		matrixStack.translate(MapUtils.startCorner.first.toFloat(), MapUtils.startCorner.second.toFloat(), 0f)

		Dungeon.Info.uniqueRooms.forEach { unique ->
			val room = unique.mainRoom
			if (legitRender && room.state.equalsOneOf(RoomState.UNDISCOVERED, RoomState.UNOPENED)) return@forEach
			val checkPos = unique.getCheckmarkPosition()
			val namePos = unique.getNamePosition()
			val xOffsetCheck = (checkPos.first / 2f) * (roomSize + CONNECTOR_SIZE)
			val yOffsetCheck = (checkPos.second / 2f) * (roomSize + CONNECTOR_SIZE)
			val xOffsetName = (namePos.first / 2f) * (roomSize + CONNECTOR_SIZE)
			val yOffsetName = (namePos.second / 2f) * (roomSize + CONNECTOR_SIZE)

			if (config.mapCheckmark) {
				RenderUtils2D.drawCheckmark(drawContext, xOffsetCheck, yOffsetCheck, room.state)
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

			// Offset + half of roomsize
			RenderUtils2D.renderCenteredText(
				drawContext,
				name,
				xOffsetName.toInt() + halfRoomSize,
				yOffsetName.toInt() + halfRoomSize,
				Color.white.rgb
			)
		}
		matrixStack.pop()
	}

	fun renderPlayerHeads(drawContext: DrawContext) {
		try {
			if (Dungeon.dungeonTeammates.isEmpty()) {
				MC.player?.let {
					RenderUtils2D.drawPlayerHead(drawContext, it.name.string, DungeonPlayer(it.skinTextures).apply {
						yaw = it.yaw
					})
				}
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
		tile: Tile,
		vertical: Boolean,
		color: Color,
	) {
		val doorwayOffset = if (roomSize == 16) 5 else 6
		val width = if (tile is Door) 6 else roomSize
		var x1 = if (vertical) x + roomSize else x
		var y1 = if (vertical) y else y + roomSize
		if (tile is Door) {
			if (vertical) y1 += doorwayOffset else x1 += doorwayOffset
		}

		RenderUtils2D.renderRect(drawContext,
			x1, y1,
			if(vertical) CONNECTOR_SIZE else width,
			if(vertical) width else CONNECTOR_SIZE,
			color.rgb
		)

//		drawContext.fill(
//			x1,
//			y1,
//			x1 + if(vertical) CONNECTOR_SIZE else width,
//			y1 + if(vertical) width else CONNECTOR_SIZE,
//			color.rgb
//		)
	}
}
