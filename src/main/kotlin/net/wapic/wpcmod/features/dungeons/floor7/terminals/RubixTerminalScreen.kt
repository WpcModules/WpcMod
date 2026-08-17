package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod

class RubixTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(menu, title) {
	private val gameArea = listOf(12, 13, 14, 21, 22, 23, 30, 31, 32)
	private var goal: Int? = null

	override fun resize(width: Int, height: Int) {
		this.height = ((menu.rowCount + 0.5f) * totalSlotSpace).toInt()
		this.width = (font.width(title) * config.customTermSize * 1.25f).toInt()
	}

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		gameArea.forEachIndexed { index, slotIndex ->
			val clicks = solution.getOrNull(index) ?: return@forEachIndexed
			val color = when(clicks) {
				1 -> config.rubixColor1
				2 -> config.rubixColor2
				-1 -> config.oppositeRubixColor1
				-2 -> config.oppositeRubixColor2
				else -> return@forEachIndexed
			}
			extractSlot(graphics, slotIndex, color, clicks.toString())
		}
	}

	override fun slotClicked(slotIndex: Int, button: Int): Boolean {
		val index = gameArea.indexOf(slotIndex).takeIf { it != -1 } ?: return false
		solution.getOrNull(index)?.let { clicks ->
			if (clicks == 0) return false
			val button = if (clicks > 0) InputConstants.MOUSE_BUTTON_LEFT else InputConstants.MOUSE_BUTTON_RIGHT
			return doTerminalClick(slotIndex, button)
		}

		return false
	}

	private fun getClicks(start: Int, goal: Int): Int {
		val size = Terminal.RUBIX_ORDER.size
		return Math.floorMod(goal - start + size / 2, size) - size / 2
	}

	override fun onInventoryUpdated(slots: List<Slot>) {
		val gameArea = slots.filterNot { it.item.item == Items.BLACK_STAINED_GLASS_PANE }
		if (goal == null) goal =
			gameArea.groupingBy { Terminal.RUBIX_ORDER.indexOf(it.item.item) }.eachCount().maxBy { it.value }.key

		goal?.let { goal ->
			solution.addAll(
				gameArea.map { slot ->
					getClicks(Terminal.RUBIX_ORDER.indexOf(slot.item.item), goal)
				}
			)
		} ?: return WpcMod.LOGGER.error("Rubix terminal goal is null!")
	}
}