package net.wapic.wpcmod.features.inventory

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.wapic.wpcmod.WpcMod

object DyeColor {

	private val config get() = WpcMod.config.inventory.dyeColor

	fun init() {
		ItemTooltipCallback.EVENT.register(::onToolTipRender)
	}

	private fun onToolTipRender(
		stack: ItemStack,
		tooltipContext: Item.TooltipContext,
		type: TooltipFlag,
		lines: MutableList<Component>
	) {
		if (!config.showLeatherColor) return

		val color = stack.get(DataComponents.DYED_COLOR) ?: return
		val hex = String.format("#%06X", color.rgb)
		var comp = Component.literal(hex)
		if (config.colorToolTip) comp = comp.withColor(color.rgb)
		lines.add(1, Component.literal("Color: ").withStyle(ChatFormatting.GRAY).append(comp))
	}
}