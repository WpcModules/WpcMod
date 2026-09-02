package net.wapic.wpcmod.features.dungeons.funnymap.ui

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.util.CommonColors
import net.minecraft.util.Mth
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.dungeon.FunnyConfig
import net.wapic.wpcmod.features.dungeons.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils
import net.wapic.wpcmod.util.skyblockId
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.drawBorder
import net.wapic.wpcmod.util.render.drawTexture
import net.wapic.wpcmod.util.render.toChromaColour
import java.awt.Color
import kotlin.math.roundToInt

object MapRenderer {

	val config get() = WpcMod.config.dungeon.funnyMap

	private val crossResource = WpcMod.Identifier("dungeon/cross.png")
	private val greenResource = WpcMod.Identifier("dungeon/green_check.png")
	private val questionResource = WpcMod.Identifier("dungeon/question.png")
	private val whiteResource = WpcMod.Identifier("dungeon/white_check.png")
	private val mapIcons = WpcMod.Identifier("dungeon/marker.png")

	fun renderCenteredText(
		drawContext: GuiGraphicsExtractor,
		text: List<String>,
		x: Int,
		y: Int,
		color: Int,
		scale: Float = 1f
	) {
		if (text.isEmpty()) return
		val player = MC.player ?: return
		val matrixStack = drawContext.pose()

		matrixStack.pushMatrix()
		matrixStack.translate(x.toFloat(), y.toFloat())
		matrixStack.scale(config.textScale * scale, config.textScale * scale)

		if (config.mapRotate) {
			matrixStack.rotate(Math.toRadians(player.yRot + 180.0).toFloat())
		}

		val font = MC.font
		val fontHeight = font.lineHeight + 1
		val yTextOffset = text.size * fontHeight / -2f

		for ((i, element) in text.withIndex()) {
			drawContext.text(
				font,
				element,
				font.width(element) / -2,
				yTextOffset.toInt() + i * fontHeight,
				color,
				true
			)
		}

		matrixStack.popMatrix()
	}

	fun drawCheckmark(drawContext: GuiGraphicsExtractor, x: Float, y: Float, state: RoomState) {
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

	fun drawPlayerHead(drawContext: GuiGraphicsExtractor, name: String, player: DungeonPlayer, deltaTicks: Float) {
		val realPlayer = MC.player ?: return
		val matrixStack = drawContext.pose()
		matrixStack.pushMatrix()

		try {

			val interpolatedX = Mth.lerp(deltaTicks, player.lastMapX, player.mapX)
			val interpolatedZ = Mth.lerp(deltaTicks, player.lastMapZ, player.mapZ)
			val interpolatedYaw = Mth.lerp(deltaTicks, player.lastYaw, player.yaw)
			matrixStack.translate(interpolatedX, interpolatedZ)

			matrixStack.scale(config.playerHeadScale, config.playerHeadScale)
			matrixStack.rotate(Math.toRadians(interpolatedYaw + 180.0).toFloat())

			if (config.mapVanillaMarker && player.isPlayer) {
				drawContext.drawTexture(mapIcons, -4, -4, 0f, 0f, 8, 8, 8, 8)
			} else {
				drawContext.drawTexture(player.skin.body.texturePath(), -4, -4, 8f, 8f, 8, 8, 64, 64)
				if (config.drawClassBorder) drawContext.drawBorder(
					-5, -5,
					10, 10,
					player.dungeonClass.color.toChromaColour()
				)
			}

			// Handle player names
			if (config.playerHeads == FunnyConfig.PlayerNameType.ALWAYS ||
				(config.playerHeads == FunnyConfig.PlayerNameType.HOLDING_LEAP && MC.heldItem.skyblockId.equalsOneOf(
					"SPIRIT_LEAP",
					"INFINITE_SPIRIT_LEAP",
					"HAUNT_ABILITY"
				))
			) {
				matrixStack.rotate(-Math.toRadians(interpolatedYaw + 180.0).toFloat())

				if (config.mapRotate) {
					matrixStack.rotate(Math.toRadians(realPlayer.yRot + 180.0).toFloat())
				}

				matrixStack.translate(0f, 6f)
				matrixStack.scale(config.playerNameScale)
				drawContext.text(
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