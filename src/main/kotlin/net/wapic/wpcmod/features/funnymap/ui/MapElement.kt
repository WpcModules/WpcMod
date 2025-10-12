package net.wapic.wpcmod.features.funnymap.ui

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.funnymap.core.map.Room
import net.wapic.wpcmod.features.funnymap.dungeon.Dungeon
import net.wapic.wpcmod.features.funnymap.dungeon.DungeonMap
import net.wapic.wpcmod.features.funnymap.dungeon.MapRender
import net.wapic.wpcmod.features.funnymap.dungeon.ScanUtils
import net.wapic.wpcmod.jarvis.SimpleHudElement
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.MC
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
		val matrixStack = drawContext.matrices

		matrixStack.push()
		applyTransformations(matrixStack)

		MapRender.renderMap(drawContext, w, h)

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
