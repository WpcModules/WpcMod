package net.wapic.wpcmod.features.kuudra

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.KuudraUtils
import net.wapic.wpcmod.util.KuudraUtils.Phase
import net.wapic.wpcmod.util.KuudraUtils.kuudraEntity
import net.wapic.wpcmod.util.Utils

class KuudraDisplay {

	private val config get() = WpcMod.config.kuudra

	private val mc = MinecraftClient.getInstance()

	init {
		HudLayerRegistrationCallback.EVENT.register { layeredDrawer ->
			layeredDrawer.attachLayerAfter(
				IdentifiedLayer.TITLE_AND_SUBTITLE,
				IdentifiedLayer.of(Identifier.of("wpcmod", "kuudra_display"), ::onRenderHud)
			)
		}
	}

	fun onRenderHud(drawContext: DrawContext, tickCounter: RenderTickCounter) {
		if (Utils.getLocation() != Island.KUUDRA || !config.healthDisplay) return

		kuudraEntity?.let {
			if (KuudraUtils.phase == Phase.KILL) {
				val x = (drawContext.scaledWindowWidth / 2) - (mc.textRenderer.getWidth(getHealthDisplay()) / 2)
				val y = (drawContext.scaledWindowHeight / 2) - (mc.textRenderer.fontHeight / 2)
				drawContext.drawTextWithShadow(mc.textRenderer, getHealthDisplay(), x, y, 0xffffffff.toInt())
			}
		}
	}

	fun getHealthDisplay(): String {
		kuudraEntity?.let {
			val formatting = when {
				it.health > 99000f -> Formatting.GREEN
				it.health > 75000f -> Formatting.DARK_GREEN
				it.health > 50000f -> Formatting.YELLOW
				it.health > 25000f -> Formatting.GOLD
				it.health > 10000f -> Formatting.RED
				else -> Formatting.DARK_RED
			}

			return "$formatting${it.health / 1000}K / §a100K §cHP"
		}

		return "NO ENTITY FOUND"
	}
}