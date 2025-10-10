package net.wapic.wpcmod.features.funnymap.ui

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.funnymap.features.dungeon.MapRender
import net.wapic.wpcmod.features.funnymap.features.dungeon.MapRenderList
import net.wapic.wpcmod.features.funnymap.features.dungeon.ScanUtils
import net.wapic.wpcmod.jarvis.SimpleHudElement
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.render.drawText

object MapElement : SimpleHudElement(
	text = Text.literal("Dungeon Map"),
	w = 128,
	h = 128
) {
	val config get() = WpcMod.config.funnyMap

	fun init() {
		HudLayerRegistrationCallback.EVENT.register { layeredDrawer ->
			layeredDrawer.attachLayerBefore(
				IdentifiedLayer.CHAT,
				IdentifiedLayer.of(Identifier.of("wpcmod", "dungeon_map"), ::render)
			)
		}
	}

	fun render(drawContext: DrawContext, tickCounter: RenderTickCounter) {
		if (!isActive) return

		val player = MinecraftClient.getInstance().cameraEntity ?: return
		drawContext.drawText("${player.blockX} ${player.blockZ}", 140, 2, 0xffffff, true)
		drawContext.drawText("core: ${ScanUtils.getCore(player.blockX, player.blockZ)}", 140, 12, 0xffffff, true)

		val matrixStack = drawContext.matrices

		matrixStack.push()
		applyTransformations(matrixStack)

		if (config.renderBeta) {
			MapRenderList.renderMap(drawContext)
		} else {
			MapRender.renderMap(drawContext, matrixStack)
		}

		matrixStack.pop()
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
