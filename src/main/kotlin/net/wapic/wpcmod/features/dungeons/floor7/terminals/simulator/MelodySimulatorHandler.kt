package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.util.MC
import kotlin.random.Random

class MelodySimulatorHandler(menu: ChestMenu, title: Component) : TerminalSimulatorHandler(menu) {
	private val pointerItem = ItemStack(Items.LIME_STAINED_GLASS_PANE)
	private val columnItem = ItemStack(Items.MAGENTA_STAINED_GLASS_PANE)
	private val rowItem = ItemStack(Items.RED_STAINED_GLASS_PANE)
	private val activeButton = ItemStack(Items.LIME_TERRACOTTA)
	private val inactiveButton = ItemStack(Items.RED_TERRACOTTA)
	private val background = ItemStack(Items.WHITE_STAINED_GLASS_PANE)

	private var currentRow = 1
	private var pointerLocation = 1
	private var currentColumn = 1
	private var forwards = true
	private var tick = 0
	private var frozenTicks = 0

	override fun onTick() {
		incrementTicks()

		if (frozenTicks > 0) return
		if (tick % 10 != 0) return

		if (pointerLocation == 5) forwards = false
		if (pointerLocation == 1) forwards = true
		pointerLocation += if (forwards) 1 else -1

		this.setSlots { slot ->
			if (slot.index / 9 == currentRow) {
				if (slot.index % 9 == pointerLocation) return@setSlots pointerItem
				if (slot.index % 9 in 1..5) return@setSlots rowItem
			}
			slot.item
		}
	}

	override fun create() {
		currentColumn = Random.nextInt(1, 5)
		this.setSlots { slot ->
			val column = slot.index % 9
			val row = slot.index / 9
			if (column % 9 == pointerLocation && row == currentRow) return@setSlots pointerItem
			if (column % 9 in 1..5 && row == currentRow) return@setSlots rowItem
			if (column == currentColumn && (row == 0 || row == 4)) return@setSlots columnItem
			if (column == 7 && row in 1..3) return@setSlots if (row == currentRow) activeButton else inactiveButton
			if (column in 1..5 && row in 1..3) return@setSlots background
			return@setSlots blackPane
		}
	}

	override fun slotClicked(slot: Slot, slotId: Int, buttonNum: Int, containerInput: ContainerInput) {
		if (slot.item.item != Items.LIME_TERRACOTTA) return
		if (currentColumn != pointerLocation) {
			MC.playSound(SoundEvents.ENDERMAN_TELEPORT, 1f, 1f)
			frozenTicks = 40
			return
		}
		playTerminalSound()
		currentRow++
		create()
	}

	override fun isTerminalSolved(slots: List<Slot>): Boolean {
		return currentRow >= 4
	}

	private fun incrementTicks() {
		if (frozenTicks > 0) {
			frozenTicks--
			return
		}
		tick++
	}
}