package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items
import net.wapic.wpcmod.features.dungeons.floor7.terminals.AbstractTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal
import net.wapic.wpcmod.util.MC

abstract class TerminalSimulatorHandler(private val menu: ChestMenu) {
	protected val emptyNameData = DataComponentPatch.builder().set(DataComponents.CUSTOM_NAME, Component.literal("")).build()
	protected val glintOverrideData = DataComponentPatch.builder().set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true).build()
	protected val blackPane = ItemStackTemplate(Items.BLACK_STAINED_GLASS_PANE, emptyNameData).create()

	abstract fun create()
	abstract fun slotClicked(slot: Slot, slotId: Int, buttonNum: Int, containerInput: ContainerInput)
	abstract fun onUpdate(slots: List<Slot>)
	open fun onTick() = Unit

	fun setSlots(block: (Slot) -> ItemStack?) {
		menu.slots.subList(0, menu.container.containerSize).forEach { slot ->
			val stack = block(slot) ?: return@forEach
			slot.setByPlayer(stack)
			(MC.screen as? AbstractTerminalScreen)?.slotChanged(menu, slot.index, stack)
		}

		onUpdate(menu.slots.subList(0, menu.container.containerSize))
	}

	fun Slot.setItem(stack: ItemStack) {
		setSlots { slot ->
			if(slot.index == this.index) stack else slot.item
		}
	}

	fun onRemoved() {
		Terminal.handler = null
	}

	fun onSolve() {
		MC.player?.clientSideCloseContainer()
	}

	companion object {
		val RUBIX_ORDER = listOf(
			Items.ORANGE_STAINED_GLASS_PANE,
			Items.YELLOW_STAINED_GLASS_PANE,
			Items.GREEN_STAINED_GLASS_PANE,
			Items.BLUE_STAINED_GLASS_PANE,
			Items.RED_STAINED_GLASS_PANE,
		)
	}
}