package net.wapic.wpcmod.features.dev

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.network.chat.Component
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ItemUtils.skyBlockID

object SkyBlockID {

	private val config get() = WpcMod.config.dev

	fun init() {
		ItemTooltipCallback.EVENT.register(::onToolTipRender)
	}

	private fun onToolTipRender(
		stack: ItemStack, tooltipContext: Item.TooltipContext, type: TooltipFlag, lines: MutableList<Component>
	) {
		if (!config.showSkyBlockID) return
		val skyBlockID = stack.skyBlockID ?: return
		lines.add(Component.nullToEmpty(skyBlockID))
	}
}