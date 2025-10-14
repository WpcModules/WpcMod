package net.wapic.wpcmod.features.funnymap.ui

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.funnymap.core.map.Door
import net.wapic.wpcmod.features.funnymap.core.map.Room
import net.wapic.wpcmod.features.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.funnymap.core.map.RoomType
import net.wapic.wpcmod.features.funnymap.core.map.Unknown
import net.wapic.wpcmod.features.funnymap.dungeon.Dungeon
import net.wapic.wpcmod.features.funnymap.dungeon.DungeonScan
import net.wapic.wpcmod.features.funnymap.utils.MapUtils
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.CONNECTOR_SIZE
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.halfRoomSize
import net.wapic.wpcmod.features.funnymap.utils.MapUtils.roomSize
import net.wapic.wpcmod.jarvis.SimpleHudElement
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.RenderUtils2D
import net.wapic.wpcmod.util.render.RenderUtils2D.darken
import net.wapic.wpcmod.util.render.RenderUtils2D.grayScale
import java.awt.Color
import kotlin.collections.component1
import kotlin.collections.component2

object MapElement : SimpleHudElement(
	text = Text.literal("Dungeon Map"),
	w = 128,
	h = 128
) {

	private val legitPeekBind: KeyBinding = KeyBindingHelper.registerKeyBinding(KeyBinding("Legit Peek", InputUtil.GLFW_KEY_J, "WpcMod"))
	val config get() = WpcMod.config.funnyMap

	val legitRender: Boolean get() = config.legitMode && !legitPeekBind.isPressed

	fun init() {
		HudLayerRegistrationCallback.EVENT.register { layeredDrawer ->
			layeredDrawer.attachLayerBefore(
				IdentifiedLayer.DEMO_TIMER,
				IdentifiedLayer.of(Identifier.of("wpcmod", "dungeon_map"), ::render)
			)
		}
	}

	fun render(drawContext: DrawContext, tickCounter: RenderTickCounter) {
		if (!isActive) return
		val player = MC.player ?: return
		val matrixStack = drawContext.matrices

		matrixStack.push()
		applyTransformations(matrixStack)

		drawContext.fill(0, 0, width, height, config.mapBackground.getEffectiveColourRGB())
		drawContext.drawBorder(0, 0, width, height, config.mapBorder.getEffectiveColourRGB())

		if (config.mapRotate) {
			matrixStack.push()
			drawContext.enableScissor(0, 0, width, height)

			matrixStack.translate(64.0, 64.0, 0.0)
			matrixStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(player.yaw + 180f))

			if(config.mapCenter) {
				matrixStack.translate(
					-((player.pos.x - DungeonScan.START_X + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.first - 2),
					-((player.pos.z - DungeonScan.START_Z + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.second - 2),
					0.0
				)
			} else {
				matrixStack.translate(-64.0, -64.0, 0.0)
			}
		}

		renderRooms(drawContext)
		renderText(drawContext)
		renderPlayerHeads(drawContext)

		if (config.mapRotate) {
			drawContext.disableScissor()
			matrixStack.pop()
		}

		matrixStack.pop()
	}

	private fun renderRooms(drawContext: DrawContext) {
		val matrixStack = drawContext.matrices
		matrixStack.push()
		matrixStack.translate(MapUtils.startCorner.first.toFloat(), MapUtils.startCorner.second.toFloat(), 0f)

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
		matrixStack.translate(-MapUtils.startCorner.first.toFloat(), -MapUtils.startCorner.second.toFloat(), 0f)
		matrixStack.pop()
	}

	private fun renderText(drawContext: DrawContext) {
		val matrixStack = drawContext.matrices
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
			RenderUtils2D.renderCenteredText(
				drawContext,
				name,
				xOffsetName.toInt() + halfRoomSize,
				yOffsetName.toInt() + halfRoomSize,
				Color.white.rgb
			)

			if (config.mapCheckmark) {
				RenderUtils2D.drawCheckmark(drawContext, xOffsetCheck, yOffsetCheck, room.state)
			}
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

	override fun isActive(): Boolean {
		if (!isEnabled || !DungeonUtils.inDungeons) return false
		if (DungeonUtils.isBossSpawned() && config.mapHideInBoss) return false
		return true
	}

	override fun isEnabled(): Boolean {
		return config.mapEnabled
	}
}
