package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.util.Util
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.features.dungeons.floor7.termsim.TermSimGUI
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import org.lwjgl.glfw.GLFW
import java.util.concurrent.CopyOnWriteArrayList

open class TerminalHandler(val type: TerminalTypes) {
    val solution: CopyOnWriteArrayList<Int> = CopyOnWriteArrayList()
    val items: Array<ItemStack?> = arrayOfNulls(type.windowSize)
	val timeOpened = Util.getMillis()
    var isClicked = false
	var containerId = 0

	open fun handleSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack): Boolean = false

    open fun simulateClick(slotIndex: Int, clickType: Int) {}

	open fun click(slotIndex: Int, button: Int, simulateClick: Boolean = true) {
		val screenHandler = (MC.screen as? ContainerScreen)?.menu ?: return
        if (simulateClick) simulateClick(slotIndex, button)
        isClicked = true

		val simulator = MC.screen as? TermSimGUI
		simulator?.let {
			it.handleClick(slotIndex, button)
			return
		}

		MC.gameMode?.handleContainerInput(
			screenHandler.containerId,
			slotIndex,
			button,
			if (button == GLFW.GLFW_MOUSE_BUTTON_3) ContainerInput.CLONE else ContainerInput.PICKUP,
			MC.player ?: return
		)
    }

    fun canClick(slotIndex: Int, button: Int, needed: Int = solution.count { it == slotIndex }): Boolean = when {
        type == TerminalTypes.MELODY -> slotIndex.equalsOneOf(16, 25, 34, 43)
        slotIndex !in solution -> false
        type == TerminalTypes.NUMBERS && slotIndex != solution.firstOrNull() -> false
        type == TerminalTypes.RUBIX && ((needed < 3 && button == 1) || (needed.equalsOneOf(3, 4) && button != 1)) -> false
        else -> true
    }
}