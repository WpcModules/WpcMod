package net.wapic.wpcmod.features.dev

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.Text
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ItemUtils.getSkyBlockID

class SkyBlockID {

	private val config get() = WpcMod.config.devConfig

	init {
		ItemTooltipCallback.EVENT.register(::onToolTipRender)
	}

	fun onToolTipRender(
		stack: ItemStack, tooltipContext: Item.TooltipContext, type: TooltipType, lines: MutableList<Text>
	) {
		if (!config.showSkyBlockID) return
		val skyBlockID = stack.getSkyBlockID() ?: return
		lines.add(Text.of(skyBlockID))
	}
}