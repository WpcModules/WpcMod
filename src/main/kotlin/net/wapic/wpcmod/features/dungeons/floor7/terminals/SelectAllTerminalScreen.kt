package net.wapic.wpcmod.features.dungeons.floor7.terminals

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import org.lwjgl.glfw.GLFW

class SelectAllTerminalScreen(menu: ChestMenu, title: Component) :
	AbstractTerminalScreen(TerminalType.SELECT_ALL, menu, title) {
	private val colorRegex = Regex("^Select all the (.+) items!$")
	private val color = colorRegex.matchEntire(title.string.replace("SILVER", "LIGHT GRAY"))?.groupValues?.get(1)
	private val dyeColor = DyeColor.entries.find { it.name.replace("_", " ").equals(color, true) }

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

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slot in items.filterNotNull()) {
			if (!isClickableItem(slot.item)) {
				if (config.debug) extractSlot(graphics, slot, ChromaColour(0f, 0f, 0f, 0, 0))
				continue
			}
			extractSlot(graphics, slot, config.selectColor)
		}
	}

	override fun slotClicked(slot: Slot, button: Int): Boolean {
		if (!isClickableItem(slot.item)) return false
		doTerminalClick(slot, GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
		return true
	}

	private fun isClickableItem(stack: ItemStack): Boolean {
		if (stack.hasFoil()) return false

		if (dyeColor == null) {
			WpcMod.LOGGER.error("Failed to get DyeColor from color {}", color)
			return false
		}

		val isCorrectColor = stack.hoverName.string.contains(dyeColor.name.replace("_", " "), true)
		val hasOverride = overrides[dyeColor]?.contains(stack.item) == true
		return isCorrectColor || hasOverride
	}
}