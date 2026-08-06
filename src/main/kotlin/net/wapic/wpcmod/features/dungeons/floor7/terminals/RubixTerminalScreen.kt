package net.wapic.wpcmod.features.dungeons.floor7.terminals

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod

class RubixTerminalScreen(menu: ChestMenu, title: Component) : AbstractTerminalScreen(Terminal.Type.RUBIX, menu, title) {
	private val solution = mutableMapOf<Int, Int>()
	val rubixColorOrder = listOf(Items.ORANGE_STAINED_GLASS_PANE, Items.YELLOW_STAINED_GLASS_PANE, Items.GREEN_STAINED_GLASS_PANE, Items.BLUE_STAINED_GLASS_PANE, Items.RED_STAINED_GLASS_PANE)
	var goal: Int? = null

	override fun extractSlots(graphics: GuiGraphicsExtractor) {
		for ((slotIndex, clicks) in solution) {
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
		solution[slotIndex]?.let { clicks ->
			val button = if (clicks > 0) InputConstants.MOUSE_BUTTON_LEFT else InputConstants.MOUSE_BUTTON_RIGHT
			if(doTerminalClick(slotIndex, button)) {
				val newClicks = clicks + if(button == 0) -1 else 1
				if(newClicks == 0) solution.remove(slotIndex)
			}
		}
		return false
	}

	fun solveItem(start: Int, goal: Int): Int? {
		val diff = goal - start

		return when {
			diff == 0 -> null
			diff > 2 -> diff - 5
			diff < -2 -> diff + 5
			else -> diff
		}
	}

	override fun onUpdate(items: Array<ItemStack?>) {
		// I'm overcomplicating things again, aren't I?
		if(goal == null) {
			goal = items.groupingBy { rubixColorOrder.indexOf(it?.item) }.eachCount().filter { it.key >= 0 }.maxBy { it.value }.key
			WpcMod.LOGGER.debug("set goal to {}", goal)
		}

		items.forEachIndexed { slotIndex, stack ->
			if (stack?.item == Items.BLACK_STAINED_GLASS_PANE) return@forEachIndexed
			val index = rubixColorOrder.indexOf(stack?.item ?: return@forEachIndexed)

			solveItem(index, goal ?: return@forEachIndexed)?.let { clicks ->
				solution[slotIndex] = clicks
				WpcMod.LOGGER.debug("set slot: {} to {}", slotIndex, clicks)
			}
		}
	}
}