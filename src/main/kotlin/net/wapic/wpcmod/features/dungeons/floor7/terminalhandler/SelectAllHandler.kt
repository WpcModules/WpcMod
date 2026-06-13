package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class SelectAllHandler(val color: DyeColor) : TerminalHandler(TerminalTypes.SELECT_ALL) {

	private val overrides = mapOf(
		DyeColor.WHITE to setOf(Items.BONE_MEAL, Items.WHITE_WOOL, Items.WHITE_CARPET, Items.WHITE_BANNER),
		DyeColor.BLACK to setOf(Items.INK_SAC),
		DyeColor.BLUE to setOf(Items.LAPIS_LAZULI),
		DyeColor.BROWN to setOf(Items.COCOA_BEANS),
		DyeColor.GREEN to setOf(Items.CACTUS),
		DyeColor.RED to setOf(Items.POPPY),
		DyeColor.YELLOW to setOf(Items.DANDELION),
	)

	override fun handleSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack): Boolean {
		if (slotId != type.windowSize - 1) return false
        solution.clear()
        solution.addAll(solveSelectAll(items, color))
        return true
    }

    override fun simulateClick(slotIndex: Int, clickType: Int) {
        solution.removeAt(solution.indexOf(slotIndex).takeIf { it != -1 } ?: return)
    }

    private fun solveSelectAll(items: Array<ItemStack?>, color: DyeColor): List<Int> {
		return items.mapIndexedNotNull { index, itemStack ->
			if (itemStack?.hasFoil() == true || itemStack?.item == Items.BLACK_STAINED_GLASS_PANE) return@mapIndexedNotNull null

			val isCorrectColor = itemStack?.hoverName?.string?.startsWith(color.name.replace("_", " "), true) == true
			val hasOverride = overrides[color]?.contains(itemStack?.item) == true

			return@mapIndexedNotNull if (isCorrectColor || hasOverride) index else null
        }
    }
}