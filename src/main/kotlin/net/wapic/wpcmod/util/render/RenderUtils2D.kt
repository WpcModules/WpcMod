package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.opengl.GlStateManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.dungeon.FunnyConfig
import net.wapic.wpcmod.features.funnymap.FunnyMap
import net.wapic.wpcmod.features.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.funnymap.dungeon.DungeonScan
import net.wapic.wpcmod.features.funnymap.utils.CheckmarkSet
import net.wapic.wpcmod.features.funnymap.utils.MapUtils
import net.wapic.wpcmod.util.ItemUtils.skyBlockID
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import java.awt.Color
import kotlin.math.roundToInt

object RenderUtils2D {

	val config get() = WpcMod.config.funnyMap

	val neuCheckmarks = CheckmarkSet(10, "neu")
	val defaultCheckmarks = CheckmarkSet(16, "default")
	val legacyCheckmarks = CheckmarkSet(8, "legacy")
	private val mapIcons = Identifier.of("wpcmod", "marker.png")
	val axis: RotationAxis = RotationAxis.POSITIVE_Z

	fun renderCenteredText(drawContext: DrawContext, text: List<String>, x: Int, y: Int, color: Int) {
		if (text.isEmpty()) return
		val player = MC.player ?: return
		val matrixStack = drawContext.matrices

		matrixStack.push()
		matrixStack.translate(x.toFloat(), y.toFloat(), 0f)
		matrixStack.scale(config.textScale, config.textScale, 1f)

		if (config.mapRotate) {
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(player.yaw))
		}

		val fontHeight = MC.textRenderer.fontHeight + 1
		val yTextOffset = text.size * fontHeight / -2f

		text.withIndex().forEach { (index, text) ->
			drawContext.drawText(
				text,
				MC.textRenderer.getWidth(text) / -2,
				yTextOffset.toInt() + index * fontHeight,
				color,
				true
			)
		}

		matrixStack.pop()
	}

	fun renderRect(drawContext: DrawContext, x: Int, y: Int, w: Int, h: Int, color: Int) {
		val matrix = drawContext.matrices.peek().positionMatrix

		 drawContext.draw { consumerProvider ->
			val vertexConsumer = consumerProvider.getBuffer(RenderLayer.getGui())
			vertexConsumer.vertex(matrix, x.toFloat(), y.toFloat() + h, 0f).color(color)
			vertexConsumer.vertex(matrix, x.toFloat() + w, y.toFloat() + h, 0f).color(color)
			vertexConsumer.vertex(matrix, x.toFloat() + w, y.toFloat(), 0f).color(color)
			vertexConsumer.vertex(matrix, x.toFloat(), y.toFloat(), 0f).color(color)
		}
	}

	fun drawCheckmark(drawContext: DrawContext, x: Float, y: Float, state: RoomState) {
		if (!config.mapCheckmark) return
		val (checkmark, size) = neuCheckmarks.getCheckmark(state) to neuCheckmarks.size

		checkmark?.let {
			drawContext.drawTexture(
				RenderLayer::getGuiTextured, it,
				x.toInt() + (MapUtils.roomSize - size) / 2,
				y.toInt() + (MapUtils.roomSize - size) / 2,
				size.toFloat(), size.toFloat(),
				size, size, size, size, size
			)
		}
	}

	fun drawPlayerHead(drawContext: DrawContext, name: String, player: DungeonPlayer) {
		val yaw = MC.player?.yaw ?: return
		val matrixStack = drawContext.matrices
		matrixStack.push()

		try {
			// Translates to the player's location which is updated every tick.
			if (player.isPlayer || name == MC.player?.name?.string) {
				MC.player?.let {
					matrixStack.translate(
						(it.pos.x - DungeonScan.START_X + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.first,
						(it.pos.z - DungeonScan.START_Z + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.second,
						0.0
					)
				}
			} else {
				matrixStack.translate(player.mapX.toFloat(), player.mapZ.toFloat(), 0f)
			}

			matrixStack.push()
			matrixStack.translate(-2f, -2f, 0f)
			matrixStack.scale(config.playerHeadScale, config.playerHeadScale, 1f)
			matrixStack.multiply(axis.rotationDegrees(yaw + 180f))

			if (config.mapVanillaMarker && (player.isPlayer || name == MC.player?.name?.string)) {
				drawContext.drawTexture(mapIcons, -4, -4, 0f, 0f, 8, 8, 8, 8)
			} else {
				drawContext.drawTexture(player.skin.texture, -4, -4, 8f, 8f, 8, 8, 64, 64)
				drawContext.drawBorder(-4, -4, 8, 8, Color.black.rgb)
			}


			// Handle player names
			if (config.playerHeads == FunnyConfig.PlayerNameType.ALWAYS ||
				(config.playerHeads == FunnyConfig.PlayerNameType.HOLDING_LEAP && MC.heldItem.skyBlockID.equalsOneOf("SPIRIT_LEAP", "INFINITE_SPIRIT_LEAP", "HAUNT_ABILITY"))
				) {
				if(!config.mapRotate) {
					matrixStack.multiply(axis.rotationDegrees(-yaw + 180f))
				}
				matrixStack.translate(0f, config.playerHeadScale * 4f, 0f)
				matrixStack.scale(config.playerNameScale, config.playerNameScale, 1f)
				drawContext.drawText(
					name,
					-MC.textRenderer.getWidth(name) / 2,
					0,
					0xffffff,
					true
				)
				matrixStack.pop()
			}

		} catch (e: Exception) {
			e.printStackTrace()
		}
		matrixStack.pop()
	}

	fun Color.grayScale(): Color {
		val gray = (red * 0.299 + green * 0.587 + blue * 0.114).roundToInt()
		return Color(gray, gray, gray, alpha)
	}

	fun Color.darken(factor: Float): Color {
		return Color((red * factor).roundToInt(), (green * factor).roundToInt(), (blue * factor).roundToInt(), alpha)
	}
}