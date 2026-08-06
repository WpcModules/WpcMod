package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.terminals.AbstractTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal

abstract class AbstractTerminalSimulator(open val screen: AbstractTerminalScreen, val type: Terminal.Type) {
	protected val emptyNameData = DataComponentPatch.builder().set(DataComponents.CUSTOM_NAME, Component.literal("")).build()
	protected val blackPane = ItemStackTemplate(Items.BLACK_STAINED_GLASS_PANE, emptyNameData).create()

	abstract fun create()
	abstract fun onClick(slotIndex: Int, button: Int)
	abstract fun onUpdate(items: Array<ItemStack?>)

	protected fun setSlots(block: (Slot) -> ItemStack?) {
		screen.menu.slots.subList(0, type.windowSize).forEach { slot ->
			val stack = block(slot) ?: return@forEach
			slot.setByPlayer(stack)
			screen.slotChanged(screen.menu, slot.index, stack)
		}
	}

	protected fun onSolve() = DungeonEvents.TERMINAL_SOLVED.invoker().onSolve(screen, true)
	fun close() = screen.onClose()
}