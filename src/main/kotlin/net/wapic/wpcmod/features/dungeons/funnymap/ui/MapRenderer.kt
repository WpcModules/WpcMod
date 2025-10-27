package net.wapic.wpcmod.features.dungeons.funnymap.ui

import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.dungeon.FunnyConfig
import net.wapic.wpcmod.features.dungeons.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.dungeons.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.DungeonScan
import net.wapic.wpcmod.features.dungeons.funnymap.utils.MapUtils
import net.wapic.wpcmod.util.ItemUtils.skyBlockID
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.Utils.equalsOneOf
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

	fun renderCenteredText(drawContext: DrawContext, text: List<String>, x: Int, y: Int, color: Int) {
		if (text.isEmpty()) return
		val player = MC.player ?: return
		val matrixStack = drawContext.matrices

		matrixStack.pushMatrix()
		matrixStack.translate(x.toFloat(), y.toFloat())
		matrixStack.scale(config.textScale, config.textScale)

		if (config.mapRotate) {
			matrixStack.rotation(player.yaw + 180f)
		}

		val tr = MC.textRenderer
		val fontHeight = tr.fontHeight + 1
		val yTextOffset = text.size * fontHeight / -2f

		for (i in 0..<text.size) {
			drawContext.drawText(
				tr,
				text[i],
				tr.getWidth(text[i]) / -2,
				yTextOffset.toInt() + i * fontHeight,
				color,
				false
			)
		}

		matrixStack.popMatrix()
	}

	fun drawCheckmark(drawContext: DrawContext, x: Float, y: Float, state: RoomState) {
		if (!config.mapCheckmark) return
		val checkmark = when (state) {
			RoomState.CLEARED -> whiteResource
			RoomState.GREEN -> greenResource
			RoomState.FAILED -> crossResource
			RoomState.UNOPENED -> if (config.legitMode) questionResource else null
			else -> null
		}

		checkmark?.let {
			drawContext.drawGuiTexture(
				RenderPipelines.GUI_TEXTURED,
				it,
				10, 10,
				0, 0,
				x.toInt() + (MapUtils.roomSize - 10) / 2,
				y.toInt() + (MapUtils.roomSize - 10) / 2,
				10, 10,
				Color.white.rgb
			)
		}
	}

	fun drawPlayerHead(drawContext: DrawContext, name: String, player: DungeonPlayer) {
		val matrixStack = drawContext.matrices
		matrixStack.pushMatrix()
		try {
			// Translates to the player's location which is updated every tick.
			if (player.isPlayer || name == MC.player?.name?.string) {
				MC.player?.let {
					matrixStack.translate(
						((it.entityPos.x - DungeonScan.START_X + 13) * MapUtils.coordMultiplier + MapUtils.startCorner.first).toFloat(),
						((it.entityPos.z - DungeonScan.START_Z + 13) * MapUtils.coordMultiplier + MapUtils.startCorner.second).toFloat(),
					)
				}
			} else {
				matrixStack.translate(player.mapX.toFloat(), player.mapZ.toFloat())
			}

			matrixStack.scale(config.playerHeadScale, config.playerHeadScale)
			matrixStack.rotation(player.yaw + 180)

			if (config.mapVanillaMarker && (player.isPlayer || name == MC.player?.name?.string)) {
				drawContext.drawTexture(mapIcons, -4, -4, 0f, 0f, 8, 8, 8, 8)
			} else {
				drawContext.drawTexture(player.skin.body.texturePath(), -4, -4, 8f, 8f, 8, 8, 64, 64)
				//drawContext.drawStrokedRectangle(-4, -4, 8, 8, Color.black.rgb)
			}


			// Handle player names
			if (config.playerHeads == FunnyConfig.PlayerNameType.ALWAYS ||
				(config.playerHeads == FunnyConfig.PlayerNameType.HOLDING_LEAP && MC.heldItem.skyBlockID.equalsOneOf("SPIRIT_LEAP", "INFINITE_SPIRIT_LEAP", "HAUNT_ABILITY"))
				) {
				if(!config.mapRotate) {
					matrixStack.rotation(-player.yaw + 180f)
				}
				matrixStack.translate(0f, config.playerHeadScale * 4f)
				matrixStack.scale(config.playerNameScale, config.playerNameScale)
				drawContext.drawText(
					MC.textRenderer,
					name,
					-MC.textRenderer.getWidth(name) / 2,
					0,
					0xffffff,
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