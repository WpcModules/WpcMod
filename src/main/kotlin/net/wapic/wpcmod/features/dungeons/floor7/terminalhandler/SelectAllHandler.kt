package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class SelectAllHandler(val color: DyeColor) : TerminalHandler(TerminalTypes.SELECT_ALL) {

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