package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class SelectAllHandler(val color: DyeColor) : TerminalHandler(TerminalTypes.SELECT_ALL) {

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
			itemStack?.let { stack ->
				val colorName = color.name.replace("_", " ")

				val nameMatches = stack.hoverName.string.startsWith(colorName, true)
				val itemMatches = when (color) {
					DyeColor.BLACK -> stack.item == Items.INK_SAC
					DyeColor.BLUE -> stack.item == Items.LAPIS_LAZULI
					DyeColor.WHITE -> stack.item == Items.BONE_MEAL || stack.item == Items.WHITE_WOOL
					DyeColor.BROWN -> stack.item == Items.COCOA_BEANS
					DyeColor.GREEN -> stack.item == Items.CACTUS
					DyeColor.RED -> stack.item == Items.POPPY || stack.item == Items.ROSE_BUSH
					DyeColor.YELLOW -> stack.item == Items.DANDELION
					DyeColor.LIGHT_GRAY -> stack.hoverName.string.startsWith("silver", true)
					else -> false
				}

				val isCorrectColor = nameMatches || itemMatches
				val isValidItem = !stack.hasFoil() && stack.item != Items.BLACK_STAINED_GLASS_PANE

				return@mapIndexedNotNull if (isValidItem && isCorrectColor) index else null
			}
		}
    }
}