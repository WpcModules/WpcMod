package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class SelectAllTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(menu, title) {

	override val gameWidth: Int = 7
	override val gameHeight: Int = 4

	private val color =
		Terminal.SELECT_ALL_PATTERN.matchEntire(title.string.replace("SILVER", "LIGHT GRAY"))?.groupValues?.get(1)
	private val dyeColor = DyeColor.entries.first { it.name.replace("_", " ").equals(color, true) }

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for (slotIndex in solution) {
			extractSlot(graphics, slotIndex, config.selectColor)
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int, input: ContainerInput): Boolean {
		if (slotIndex in solution) {
			solution.removeIf { it == slotIndex }
			doTerminalClick(slotIndex, InputConstants.MOUSE_BUTTON_MIDDLE, ContainerInput.CLONE)
			return true
		}
		return false
	}

	override fun solveTerminal(slots: List<Slot>): List<Int> {
		return slots.mapNotNull { slot -> slot.index.takeIf { hasColor(slot.item) } }
	}

	fun hasColor(stack: ItemStack?): Boolean {
		val isCorrectColor = stack?.hoverName?.string?.startsWith(dyeColor.name.replace("_", " "), true) == true
		val hasOverride = ITEM_OVERRIDES[dyeColor]?.contains(stack?.item) == true

		return isCorrectColor || hasOverride
	}

	companion object {
		private val ITEM_OVERRIDES = mapOf(
			// All this because Hypixel hates us. some of these items might not even be in the damn menu. I just put them there to be sure
			DyeColor.WHITE to setOf(
				Items.BONE_MEAL,
				Items.WHITE_WOOL,
				Items.WHITE_CARPET,
				Items.WHITE_BANNER
			),
			DyeColor.BLACK to setOf(Items.INK_SAC),
			DyeColor.BLUE to setOf(Items.LAPIS_LAZULI),
			DyeColor.BROWN to setOf(Items.COCOA_BEANS),

			// Green/Red/Yellow Dye still uses legacy names, so .startsWith won't match.
			DyeColor.GREEN to setOf(Items.GREEN_DYE),
			DyeColor.RED to setOf(Items.RED_DYE),
			DyeColor.YELLOW to setOf(Items.YELLOW_DYE),

			// Light Gray is called Silver, because why not?? except Light Gray Dye cause fuck me, I guess.
			DyeColor.LIGHT_GRAY to setOf(
				Items.LIGHT_GRAY_STAINED_GLASS_PANE,
				Items.LIGHT_GRAY_STAINED_GLASS,
				Items.LIGHT_GRAY_TERRACOTTA,
				Items.LIGHT_GRAY_WOOL
			),
		)
	}
}