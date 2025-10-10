package net.wapic.wpcmod.util.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.dungeon.FunnyConfig
import net.wapic.wpcmod.features.funnymap.FunnyMap
import net.wapic.wpcmod.features.funnymap.core.DungeonPlayer
import net.wapic.wpcmod.features.funnymap.core.map.RoomState
import net.wapic.wpcmod.features.funnymap.features.dungeon.DungeonScan
import net.wapic.wpcmod.features.funnymap.utils.CheckmarkSet
import net.wapic.wpcmod.features.funnymap.utils.MapUtils
import net.wapic.wpcmod.features.funnymap.utils.Utils.equalsOneOf
import net.wapic.wpcmod.util.ItemUtils.skyBlockID
import org.joml.Quaternionf
import java.awt.Color
import kotlin.math.roundToInt

object RenderUtils2D {

	val config get() = WpcMod.config.funnyMap

	val neuCheckmarks = CheckmarkSet(10, "neu")
	val defaultCheckmarks = CheckmarkSet(16, "default")
	val legacyCheckmarks = CheckmarkSet(8, "legacy")
	private val mapIcons = Identifier.of("wpcmod", "marker.png")

	fun addQuadVertices(x: Double, y: Double, w: Double, h: Double) {
		/*
        worldRenderer.pos(x, y + h, 0.0).endVertex()
        worldRenderer.pos(x + w, y + h, 0.0).endVertex()
        worldRenderer.pos(x + w, y, 0.0).endVertex()
        worldRenderer.pos(x, y, 0.0).endVertex()
		 */
	}

	fun drawTexturedQuad(x: Double, y: Double, width: Double, height: Double) {
		/*
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(x, y + height, 0.0).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(x + width, y + height, 0.0).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(x + width, y, 0.0).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(x, y, 0.0).tex(0.0, 0.0).endVertex()
        tessellator.draw()
        */
	}

	fun renderRect(drawContext: DrawContext, x: Number, y: Number, w: Number, h: Number, color: Color) {
		drawContext.fill(x as Int, y as Int, x + w as Int, y + h as Int, color.rgb)
	}

	fun renderRectBorder(
		drawContext: DrawContext,
		x: Double,
		y: Double,
		w: Double,
		h: Double,
		thickness: Double,
		color: Color
	) {
		drawContext.drawBorder(x.toInt(), y.toInt(), (x + w).toInt(), (y + h).toInt(), color.rgb)
	}

	fun renderCenteredText(drawContext: DrawContext, text: List<String>, x: Int, y: Int, color: Int) {
		if (text.isEmpty()) return
		val mcPlayer = MinecraftClient.getInstance().player ?: return
		val matrixStack = drawContext.matrices
		matrixStack.push()
		matrixStack.translate(x.toFloat(), y.toFloat(), 0f)
		matrixStack.scale(config.textScale, config.textScale, 1f)

		if (config.mapRotate) {
			// matrixStack.peek().rotate(mcPlayer.yaw + 180f, 0f, 0f, 1f)
			val rotation = Quaternionf()
			rotation.x = mcPlayer.yaw + 180f
			matrixStack.multiply(rotation)
		}

		val fontHeight = FunnyMap.mc.textRenderer.fontHeight + 1
		val yTextOffset = text.size * fontHeight / -2f

		text.withIndex().forEach { (index, text) ->
			drawContext.drawText(
				text,
				FunnyMap.mc.textRenderer.getWidth(text) / -2,
				(yTextOffset + index + fontHeight).toInt(),
				color,
				true
			)
		}

		matrixStack.pop()
	}

	fun drawCheckmark(drawContext: DrawContext, x: Float, y: Float, state: RoomState) {
		if (!config.mapCheckmark) return
		val (checkmark, size) = neuCheckmarks.getCheckmark(state) to neuCheckmarks.size

		checkmark?.let {
			drawContext.drawTexture(
				RenderLayer::getGuiTextured, it,
				(x + (MapUtils.roomSize - size) / 2).toInt(),
				(y + (MapUtils.roomSize - size) / 2).toInt(),
				size.toFloat(), size.toFloat(),
				size, size, size, size, size
			)
		}
	}

	fun drawPlayerHead(drawContext: DrawContext, name: String, player: DungeonPlayer) {
		val mcPlayer = MinecraftClient.getInstance().player ?: return

		val matrixStack = drawContext.matrices
		matrixStack.push()

		try {
			// Translates to the player's location which is updated every tick.
			if (player.isPlayer || name == mcPlayer.name.string) {
				matrixStack.translate(
					(mcPlayer.pos.x - DungeonScan.START_X + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.first,
					(mcPlayer.pos.z - DungeonScan.START_Z + 15) * MapUtils.coordMultiplier + MapUtils.startCorner.second,
					0.0
				)
			} else {
				matrixStack.translate(player.mapX.toFloat(), player.mapZ.toFloat(), 0f)
			}

			// Apply head rotation and scaling
			//matrixStack.peek().rotate(player.yaw + 180f, 0f, 0f, 1f)
			matrixStack.scale(config.playerHeadScale, config.playerHeadScale, 1f)

			if (config.mapVanillaMarker && (player.isPlayer || name == FunnyMap.mc.player?.name?.string)) {
				/*
                matrixStack.peek().rotate(180f, 0f, 0f, 1f)
                FunnyMap.mc.textureManager.bindTexture(mapIcons)
                worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX) // TODO: figure out why this is a thing
                worldRenderer.pos(-6.0, 6.0, 0.0).tex(0.0, 0.0).endVertex()
                worldRenderer.pos(6.0, 6.0, 0.0).tex(1.0, 0.0).endVertex()
                worldRenderer.pos(6.0, -6.0, 0.0).tex(1.0, 1.0).endVertex()
                worldRenderer.pos(-6.0, -6.0, 0.0).tex(0.0, 1.0).endVertex()
                tessellator.draw()
                matrixStack.peek().rotate(-180f, 0f, 0f, 1f)
				 */
			} else {
				// Render black border around the player head
				renderRectBorder(drawContext, -6.0, -6.0, 12.0, 12.0, 1.0, Color(0, 0, 0, 255))

				//FunnyMap.mc.textureManager.bindTexture(player.skin)
				/*
				Gui.drawScaledCustomSizeModalRect(-6, -6, 8f, 8f, 8, 8, 12, 12, 64f, 64f)
                if (player.renderHat) {
                    Gui.drawScaledCustomSizeModalRect(-6, -6, 40f, 8f, 8, 8, 12, 12, 64f, 64f)
                }
				 */
			}

			// Handle player names
			if (config.playerHeads == FunnyConfig.PlayerNameType.ALWAYS || config.playerHeads == FunnyConfig.PlayerNameType.HOLDING_LEAP && FunnyMap.mc.player?.mainHandStack?.skyBlockID.equalsOneOf(
					"SPIRIT_LEAP", "INFINITE_SPIRIT_LEAP", "HAUNT_ABILITY"
				)
			) {
				if (!config.mapRotate) {
					//matrixStack.rotate(-player.yaw + 180f, 0f, 0f, 1f)
				}
				matrixStack.translate(0f, 10f, 0f)
				matrixStack.scale(config.playerNameScale, config.playerNameScale, 1f)
				drawContext.drawText(
					name,
					-FunnyMap.mc.textRenderer.getWidth(name) / 2,
					0,
					0xffffff,
					true
				)
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