package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class SelectAllTerminalScreen(menu: ChestMenu, title: Component) :
	AbstractTerminalScreen(Terminal.Type.SELECT_ALL, menu, title) {
	private val selectAllRegex = Regex("^Select all the (.+) items!$")
	private val color = selectAllRegex.matchEntire(title.string.replace("SILVER", "LIGHT GRAY"))?.groupValues?.get(1)
	private val solution = mutableListOf<Int>()

	private val overrides = mapOf(
		// All this because Hypixel hates us.
		DyeColor.WHITE to setOf(Items.BONE_MEAL, Items.WHITE_WOOL, Items.WHITE_CARPET, Items.WHITE_BANNER),
		DyeColor.BLACK to setOf(Items.INK_SAC),
		DyeColor.BLUE to setOf(Items.LAPIS_LAZULI),
		DyeColor.BROWN to setOf(Items.COCOA_BEANS),

		// Green/Red/Yellow Dye still uses legacy names, so .startsWith won't match.
		// As for the actual items(cactus, poppy, dandelion) I'm unsure if they're used, so I left them.
		DyeColor.GREEN to setOf(Items.GREEN_DYE, Items.CACTUS),
		DyeColor.RED to setOf(Items.RED_DYE, Items.POPPY),
		DyeColor.YELLOW to setOf(Items.YELLOW_DYE, Items.DANDELION),

		// Light Gray is called Silver, because why not?? except Light Gray Dye cause fuck me, I guess.
		DyeColor.LIGHT_GRAY to setOf(
			Items.LIGHT_GRAY_STAINED_GLASS_PANE,
			Items.LIGHT_GRAY_STAINED_GLASS,
			Items.LIGHT_GRAY_TERRACOTTA,
			Items.LIGHT_GRAY_WOOL
		),
	)

	val dyeColor = DyeColor.entries.first { it.name.replace("_", " ").equals(color, true) }

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slotIndex in solution) {
			extractSlot(graphics, slotIndex, config.selectColor)
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int): Boolean {
		if (slotIndex !in solution) return false
		if (doTerminalClick(slotIndex, InputConstants.MOUSE_BUTTON_MIDDLE)) {
			return solution.removeIf { it == slotIndex }
		}
		return false
	}

	override fun onUpdate(items: Array<ItemStack?>) {
		solution.addAll(items.mapIndexedNotNull{ index, stack -> if(isValidItem(stack)) index else null })
	}

	fun isValidItem(stack: ItemStack?): Boolean {
		if (stack?.hasFoil() == true) return false

		val isCorrectColor = stack?.hoverName?.string?.startsWith(dyeColor.name.replace("_", " "), true) == true
		val hasOverride = overrides[dyeColor]?.contains(stack?.item) == true

		return isCorrectColor || hasOverride
	}
}