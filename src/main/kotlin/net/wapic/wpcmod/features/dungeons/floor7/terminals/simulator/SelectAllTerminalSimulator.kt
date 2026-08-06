package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.features.dungeons.floor7.terminals.SelectAllTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal
import kotlin.random.Random

class SelectAllTerminalSimulator(override val screen: SelectAllTerminalScreen) : AbstractTerminalSimulator(screen, Terminal.Type.SELECT_ALL) {
	private val dyedItems = BuiltInRegistries.ITEM.getTagOrEmpty(ConventionalItemTags.DYED)
	private val incorrectColors = DyeColor.entries.filterNot { it == screen.dyeColor }

	override fun create() {
		this.setSlots { slot ->
			if(slot.index % 9 in 1..7 && slot.index / 9 in 1..4) {
				val color = if(Random.nextDouble() > 0.65) screen.dyeColor else incorrectColors.random()
				return@setSlots getRandomItemOfColor(color)
			}
			return@setSlots blackPane
		}
	}

	override fun onClick(slotIndex: Int, button: Int) {
		this.setSlots { slot ->
			if(slot.index == slotIndex) slot.item.apply { set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true) } else slot.item
		}
	}

	override fun onUpdate(items: Array<ItemStack?>) {
		if(items.none(screen::isValidItem)) this.onSolve()
	}

	private fun getRandomItemOfColor(color: DyeColor): ItemStack {
		return dyedItems.filter { it.`is`(getColor(color)) }.random().value().defaultInstance
	}

	private fun getColor(color: DyeColor): TagKey<Item> {
		return when (color) {
			DyeColor.WHITE -> ConventionalItemTags.WHITE_DYED
			DyeColor.ORANGE -> ConventionalItemTags.ORANGE_DYED
			DyeColor.MAGENTA -> ConventionalItemTags.MAGENTA_DYED
			DyeColor.LIGHT_BLUE -> ConventionalItemTags.LIGHT_BLUE_DYED
			DyeColor.YELLOW -> ConventionalItemTags.YELLOW_DYED
			DyeColor.LIME -> ConventionalItemTags.LIME_DYED
			DyeColor.PINK -> ConventionalItemTags.PINK_DYED
			DyeColor.GRAY -> ConventionalItemTags.GRAY_DYED
			DyeColor.LIGHT_GRAY -> ConventionalItemTags.LIGHT_GRAY_DYED
			DyeColor.CYAN -> ConventionalItemTags.CYAN_DYED
			DyeColor.PURPLE -> ConventionalItemTags.PURPLE_DYED
			DyeColor.BLUE -> ConventionalItemTags.BLUE_DYED
			DyeColor.BROWN -> ConventionalItemTags.BROWN_DYED
			DyeColor.GREEN -> ConventionalItemTags.GREEN_DYED
			DyeColor.RED -> ConventionalItemTags.RED_DYED
			DyeColor.BLACK -> ConventionalItemTags.BLACK_DYED
		}
	}
}