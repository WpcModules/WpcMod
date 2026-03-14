package net.wapic.wpcmod.features.dungeons.floor7.termsim

import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalTypes
import net.wapic.wpcmod.util.ChatUtils
import kotlin.math.floor

class SelectAllSim(
    private val color: DyeColor = DyeColor.entries.random()
) : TermSimGUI(
    "Select all the ${color.name.replace("_", " ")} items!",
	TerminalTypes.SELECT_ALL.windowSize
) {

	override fun create() {
		val guaranteed = (10..16).plus(19..25).plus(28..34).plus(37..43).random()
		createNewGui { slot ->
			if (floor(slot.containerSlot / 9.0) in 1.0..4.0 && slot.containerSlot % 9 in 1..7) {
				val item = ItemStack(getPossibleItems(color).random())

				if (slot.containerSlot == guaranteed) {
					item
				} else {
					if (Math.random() > 0.75) {
						item
					} else {
						ItemStack(getPossibleItems(DyeColor.entries.filter { it != color }.random()).random())
					}
				}
			} else blackPane
		}
	}

	override fun slotClick(slot: Slot, button: Int) {
		val stack = slot.item ?: return
		val possibleItems = getPossibleItems(color)
		if (!possibleItems.contains(stack.item)) return ChatUtils.sendMessage("§cThat item is not: ${color.name.uppercase()}!")
		createNewGui {
			if (it == slot) { stack.apply { set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true) } } else it.item
		}

		playTermSimSound()

		if (guiInventorySlots.none { it?.item?.hasFoil() == false && possibleItems.contains(it.item?.item) }) {
			this@SelectAllSim.onTerminalSolved()
		}
	}

	private fun getPossibleItems(color: DyeColor): List<Item> {
		return listOf(
			BuiltInRegistries.ITEM.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "${color.name.lowercase()}_stained_glass")),
			BuiltInRegistries.ITEM.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "${color.name.lowercase()}_wool")),
			BuiltInRegistries.ITEM.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "${color.name.lowercase()}_terracotta")),
			when (color) {
				DyeColor.WHITE -> Items.BONE_MEAL
				DyeColor.BLUE -> Items.LAPIS_LAZULI
				DyeColor.BLACK -> Items.INK_SAC
				DyeColor.BROWN -> Items.COCOA_BEANS
				else -> BuiltInRegistries.ITEM.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "${color.name.lowercase()}_dye"))
			}
		)
	}
}
