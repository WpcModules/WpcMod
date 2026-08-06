package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod

class RubixTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(menu, title) {
	private var rubixSolution = mapOf<Int, Int>()

	var goal: Int? = null

	override fun resize(width: Int, height: Int) {
		this.height = ((menu.rowCount + 0.5f) * totalSlotSpace).toInt()
		this.width = (font.width(title) * config.customTermSize * 1.25f).toInt()
	}

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for ((slotIndex, clicks) in rubixSolution) {
			val color = when(clicks) {
				1 -> config.rubixColor1
				2 -> config.rubixColor2
				-1 -> config.oppositeRubixColor1
				-2 -> config.oppositeRubixColor2
				else -> continue
			}
			extractSlot(graphics, slotIndex, color, clicks.toString())
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int): Boolean {
		rubixSolution[slotIndex]?.let { clicks ->
			if(clicks == 0) return false
			val button = if (clicks > 0) InputConstants.MOUSE_BUTTON_LEFT else InputConstants.MOUSE_BUTTON_RIGHT
			return doTerminalClick(slotIndex, button)
		}
		return false
	}

	fun getClicks(start: Int, goal: Int): Int {
		return (goal - start + RUBIX_ORDER.size / 2) % RUBIX_ORDER.size - RUBIX_ORDER.size / 2
	}

	override fun onUpdate(slots: List<Slot>) {
		val gameArea = slots.filterNot { it.item.item == Items.BLACK_STAINED_GLASS_PANE }
		goal = gameArea.groupingBy { RUBIX_ORDER.indexOf(it.item.item) }.eachCount().filter { it.key >= 0 }.maxBy { it.value }.key

		goal?.let { goal ->
			rubixSolution = gameArea.associate { slot ->
				slot.index to getClicks(RUBIX_ORDER.indexOf(slot.item.item), goal)
			}
		} ?: return WpcMod.LOGGER.error("Rubix terminal goal is null!")
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