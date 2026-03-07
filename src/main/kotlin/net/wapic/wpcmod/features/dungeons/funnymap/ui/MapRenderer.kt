package net.wapic.wpcmod.features.dungeons.funnymap.ui

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.CommonColors
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.dungeon.FunnyConfig
import net.wapic.wpcmod.features.dungeons.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.DungeonScan
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils
import net.wapic.wpcmod.util.ItemUtils.skyblockId
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.BLACK
import net.wapic.wpcmod.util.render.drawBorder
import net.wapic.wpcmod.util.render.drawTexture
import java.awt.Color
import kotlin.math.roundToInt

object MapRenderer {

	val config get() = WpcMod.config.dungeon.funnyMap

	private val crossResource = Utils.modIdentifier("dungeon/cross.png")
	private val greenResource = Utils.modIdentifier("dungeon/green_check.png")
	private val questionResource = Utils.modIdentifier("dungeon/question.png")
	private val whiteResource = Utils.modIdentifier("dungeon/white_check.png")
	private val mapIcons = Utils.modIdentifier("dungeon/marker.png")

	fun renderCenteredText(drawContext: GuiGraphics, text: List<String>, x: Int, y: Int, color: Int) {
		if (text.isEmpty()) return
		val player = MC.player ?: return
		val matrixStack = drawContext.pose()

		matrixStack.pushMatrix()
		matrixStack.translate(x.toFloat(), y.toFloat())
		matrixStack.scale(config.textScale, config.textScale)

		if (config.mapRotate) {
			matrixStack.rotate(Math.toRadians(player.yRot + 180.0).toFloat())
		}

		val font = MC.font
		val fontHeight = font.lineHeight + 1
		val yTextOffset = text.size * fontHeight / -2f

		for (i in 0..<text.size) {
			drawContext.drawString(
				font,
				text[i],
				font.width(text[i]) / -2,
				yTextOffset.toInt() + i * fontHeight,
				color,
				true
			)
		}

		matrixStack.popMatrix()
	}

	fun drawCheckmark(drawContext: GuiGraphics, x: Float, y: Float, state: RoomState) {
		if (!config.mapCheckmark) return
		val checkmark = when (state) {
			RoomState.CLEARED -> whiteResource
			RoomState.GREEN -> greenResource
			RoomState.FAILED -> crossResource
			RoomState.UNOPENED -> if (config.legitMode) questionResource else null
			else -> null
		}

		checkmark?.let {
			drawContext.drawTexture(
				it,
				x.toInt() + (MapUtils.roomSize - 10) / 2,
				y.toInt() + (MapUtils.roomSize - 10) / 2,
				0f, 0f,
				10, 10,
				10, 10
			)
		}
	}

	fun drawPlayerHead(drawContext: GuiGraphics, name: String, player: DungeonPlayer) {
		val matrixStack = drawContext.pose()
		matrixStack.pushMatrix()
		val playerYaw = MC.player?.yRot ?: return

		try {
			// Translates to the player's location which is updated every tick.
			if (player.isPlayer || name == MC.player?.name?.string) {
				MC.player?.let {
					matrixStack.translate(
						((it.x - DungeonScan.START_X + 13) * MapUtils.coordMultiplier + MapUtils.startCorner.first).toFloat(),
						((it.z - DungeonScan.START_Z + 13) * MapUtils.coordMultiplier + MapUtils.startCorner.second).toFloat(),
					)
				}
			} else {
				matrixStack.translate(player.mapX.toFloat(), player.mapZ.toFloat())
			}

			matrixStack.scale(config.playerHeadScale, config.playerHeadScale)
			matrixStack.rotate(Math.toRadians(player.yaw + 180.0).toFloat())

			if (config.mapVanillaMarker && (player.isPlayer || name == MC.player?.name?.string)) {
				drawContext.drawTexture(mapIcons, -4, -4, 0f, 0f, 8, 8, 8, 8)
			} else {
				drawContext.drawTexture(player.skin.body.texturePath(), -4, -4, 8f, 8f, 8, 8, 64, 64)
				drawContext.drawBorder(-4, -4, 8, 8, ChromaColour.BLACK)
			}

			// Handle player names
			if (config.playerHeads == FunnyConfig.PlayerNameType.ALWAYS ||
				(config.playerHeads == FunnyConfig.PlayerNameType.HOLDING_LEAP && MC.heldItem.skyblockId.equalsOneOf(
					"SPIRIT_LEAP",
					"INFINITE_SPIRIT_LEAP",
					"HAUNT_ABILITY"
				))
				) {
				matrixStack.rotate(-Math.toRadians(player.yaw + 180.0).toFloat())

				if (config.mapRotate) {
					matrixStack.rotate(Math.toRadians(playerYaw + 180.0).toFloat())
				}

				matrixStack.translate(0f, config.playerHeadScale * 4f)
				matrixStack.scale(config.playerNameScale, config.playerNameScale)
				drawContext.drawString(
					MC.font,
					name,
					-MC.font.width(name) / 2,
					0,
					CommonColors.WHITE,
					true
				)
			}

		} catch (e: Exception) {
			e.printStackTrace()
		}
		matrixStack.popMatrix()
	}

	fun Color.grayScale(): Color {
		val gray = (red * 0.299 + green * 0.587 + blue * 0.114).roundToInt()
		return Color(gray, gray, gray, alpha)
	}

	fun Color.darken(factor: Float): Color {
		return Color((red * factor).roundToInt(), (green * factor).roundToInt(), (blue * factor).roundToInt(), alpha)
	}
}