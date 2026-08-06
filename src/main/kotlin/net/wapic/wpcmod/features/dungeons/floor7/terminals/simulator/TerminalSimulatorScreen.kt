package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal
import net.wapic.wpcmod.util.MC

class TerminalSimulatorScreen(menu: ChestMenu, inventory: Inventory, title: Component) : ContainerScreen(menu, inventory, title) {

	override fun init() {
		super.init()
		Terminal.handler?.create()
	}

	override fun containerTick() {
		super.containerTick()
		Terminal.handler?.onTick()
	}

	override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
		this.hoveredSlot?.let {
			val input = if(event.button() == 0 || event.button() == 1) ContainerInput.PICKUP else ContainerInput.CLONE
			Terminal.handler?.slotClicked(it, it.index, event.button(), input)
			MC.playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1f)
			return true
		}
		return false
	}

	override fun removed() {
		Terminal.handler?.onRemoved()
		super.removed()
	}

	override fun onClose() {
		minecraft.player?.clientSideCloseContainer()
	}
}