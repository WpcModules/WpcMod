package net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.features.dungeons.floor7.terminals.MelodyTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.terminals.Terminal
import net.wapic.wpcmod.util.Utils.equalsOneOf
import kotlin.random.Random

class MelodyTerminalSimulator(override val screen: MelodyTerminalScreen) : AbstractTerminalSimulator(screen, Terminal.Type.MELODY) {

	private val pointerItem = ItemStack(Items.LIME_STAINED_GLASS_PANE)
	private val columnItem = ItemStack(Items.MAGENTA_STAINED_GLASS_PANE)
	private val rowItem = ItemStack(Items.RED_STAINED_GLASS_PANE)
	private val activeButton = ItemStack(Items.LIME_TERRACOTTA)
	private val inactiveButton = ItemStack(Items.RED_TERRACOTTA)
	private val background = ItemStack(Items.WHITE_STAINED_GLASS_PANE)

	private var currentColumn = -1
	private var currentRow = 1
	private var pointerLocation = 1
	private var forwards = true

	fun tick() {
		if(pointerLocation == 5) forwards = false
		if(pointerLocation == 1) forwards = true
		pointerLocation += if(forwards) 1 else -1
		this.setSlots { slot ->
			if(slot.index / 9 == currentRow) {
				if(slot.index % 9 == pointerLocation) return@setSlots pointerItem
				if(slot.index % 9 in 1..5) return@setSlots rowItem
			}
			slot.item
		}
	}

	override fun create() {
		currentColumn = Random.nextInt(1, 5)
		this.setSlots { slot ->
			val column = slot.index % 9
			val row = slot.index / 9
			if(column == currentColumn && row.equalsOneOf(0, 5)) return@setSlots columnItem
			if(column in 1..5 && row == currentRow) return@setSlots if(column == pointerLocation) pointerItem else rowItem
			if(column == 7 && row in 1..4) return@setSlots if(row == currentRow) activeButton else inactiveButton
			if(column in 1..5 && row in 1..4) return@setSlots background
			return@setSlots blackPane
		}
	}

	override fun onClick(slotIndex: Int, button: Int) {
		currentRow += 1
		if(currentRow == 5) {
			return this.onSolve()
		}
		this.create()
	}

	override fun onUpdate(items: Array<ItemStack?>) = Unit
}