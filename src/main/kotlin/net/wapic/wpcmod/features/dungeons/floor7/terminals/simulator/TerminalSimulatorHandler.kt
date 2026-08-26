package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal
import net.wapic.wpcmod.util.MC

abstract class TerminalSimulatorHandler(private val menu: ChestMenu) {
	protected val emptyNameData =
		DataComponentPatch.builder().set(DataComponents.CUSTOM_NAME, Component.literal("")).build()
	protected val glintOverrideData =
		DataComponentPatch.builder().set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true).build()
	protected val blackPane = ItemStackTemplate(Items.BLACK_STAINED_GLASS_PANE, emptyNameData).create()
	private val slots: List<Slot> get() = menu.slots.subList(0, menu.container.containerSize)

	abstract fun create()
	abstract fun slotClicked(slot: Slot, slotId: Int, buttonNum: Int, containerInput: ContainerInput)
	abstract fun isTerminalSolved(slots: List<Slot>): Boolean
	open fun onTick() = Unit

	fun removed() {
		Terminal.handler = null
	}

	protected fun setSlots(block: (Slot) -> ItemStack?) {
		for (slot in slots) {
			slot.setByPlayer(block(slot) ?: continue)
			GuiEvents.SLOT_UPDATE.invoker().onSlotUpdate(Integer.MAX_VALUE, slot.index, slot.item)
		}

		if (isTerminalSolved(slots)) {
			MC.player?.clientSideCloseContainer()
		}
	}

	protected fun playTerminalSound() {
		MC.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), 1f, 2f)
	}

	protected fun Slot.setItem(stack: ItemStack) {
		setSlots { slot ->
			if (slot.index == this.index) stack else slot.item
		}
	}
}