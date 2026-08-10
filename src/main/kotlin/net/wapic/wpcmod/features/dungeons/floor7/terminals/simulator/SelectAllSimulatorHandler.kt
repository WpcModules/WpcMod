package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.DyeItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.StainedGlassBlock
import net.minecraft.world.level.block.StainedGlassPaneBlock
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal
import kotlin.random.Random

class SelectAllSimulatorHandler(menu: ChestMenu, title: Component) : TerminalSimulatorHandler(menu) {

	private val color = Terminal.SELECT_ALL_PATTERN.matchEntire(title.string)?.groupValues?.get(1)
	private val dyeColor = DyeColor.entries.find { it.name.replace("_", " ") == color } ?: DyeColor.entries.random()
	private val allItems = BuiltInRegistries.ITEM.map { it.defaultInstance }
	private val incorrectColors = DyeColor.entries.filterNot { it == dyeColor }

	override fun create() {
		this.setSlots { slot ->
			if(slot.index % 9 in 1..7 && slot.index / 9 in 1..4) {
				val color = if(Random.nextDouble() > 0.65) dyeColor else incorrectColors.random()
				return@setSlots getRandomItemOfColor(color)
			}
			return@setSlots blackPane
		}
	}

	override fun slotClicked(slot: Slot, slotId: Int, buttonNum: Int, containerInput: ContainerInput) {
		if(hasColorWithoutFoil(slot)) {
			slot.item.applyComponents(glintOverrideData)
			slot.setItem(slot.item)
		}
	}

	override fun isTerminalSolved(slots: List<Slot>): Boolean {
		return slots.none(::hasColorWithoutFoil)
	}

	private fun hasColorWithoutFoil(slot: Slot): Boolean {
		return slot.item.hoverName.string.startsWith(dyeColor.name.replace("_", " "), true) && !slot.item.hasFoil()
	}

	private fun getRandomItemOfColor(color: DyeColor): ItemStack {
		val dyeItems = allItems.filter(::getDyeItems)
		return dyeItems.filter { it.hoverName.string.startsWith(color.name.replace("_", " "), true) }.random().copy()
	}

	// TODO: find a better way to get color items (for some reason hypixel doesn't have the DYED tag sometimes??)
	private fun getDyeItems(stack: ItemStack): Boolean {
		val block = (stack.item as? BlockItem)?.block
		if (block is StainedGlassBlock) return true
		if (block is StainedGlassPaneBlock && block != Blocks.BLACK_STAINED_GLASS) return true
		if (stack.item is DyeItem) return true
		return false

	}
}