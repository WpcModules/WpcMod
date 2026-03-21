package net.wapic.wpcmod.features.dungeons.floor7.termsim

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.SimpleContainer
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSolver
import net.wapic.wpcmod.util.MC

open class TermSimGUI(
    val name: String,
    val size: Int,
    private val inv: SimpleContainer = SimpleContainer(size)
) : ContainerScreen(
    ChestMenu(
        if (size <= 9) MenuType.GENERIC_9x1
        else if (size <= 18) MenuType.GENERIC_9x2
        else if (size <= 27) MenuType.GENERIC_9x3
        else if (size <= 36) MenuType.GENERIC_9x4
        else if (size <= 45) MenuType.GENERIC_9x5
        else MenuType.GENERIC_9x6,
        0, MC.player!!.inventory, inv, size / 9
    ),
    MC.player!!.inventory,
    Component.literal(name)
) {
    val blackPane = ItemStack(Items.BLACK_STAINED_GLASS_PANE).apply { set(DataComponents.CUSTOM_NAME, Component.literal("")) }
	val guiInventorySlots get() = menu.slots.subList(0, size)
    private var doesAcceptClick = true
    protected var ping = 0L
	private var containerId = 0

    open fun create() {
        guiInventorySlots.forEach { it.setSlot(blackPane) }
    }

    fun open(terminalPing: Long = 0L) {
		MC.instance.schedule {
			MC.screen = this
			create()
			ping = terminalPing
		}
    }

	protected fun onTerminalSolved() {
        if (MC.screen !== this) return
		TerminalSolver.lastTermOpened?.let { DungeonEvents.TERMINAL_SOLVED.invoker().onSolve(it) }
		GuiEvents.CLOSE.invoker().onClose()
        StartGUI.open(ping)
    }

    open fun slotClick(slot: Slot, button: Int) {}

    override fun onClose() {
        doesAcceptClick = true
        super.onClose()
    }

	fun handleClick(slotId: Int, button: Int) {
		if (MC.screen !== this) return
		val slot = guiInventorySlots.getOrNull(slotId) ?: return
		delaySlotClick(slot, button)
	}

	private fun delaySlotClick(slot: Slot, button: Int) = WpcMod.coroutineScope.launch {
		if (MC.screen == StartGUI) return@launch slotClick(slot, button)
		if (!doesAcceptClick || slot.container != inv || slot.item.item == Items.BLACK_STAINED_GLASS_PANE) return@launch
        doesAcceptClick = false

		delay((ping).coerceAtLeast(0))
		if (MC.screen != this@TermSimGUI) return@launch
		doesAcceptClick = true
		slotClick(slot, button)
    }

	override fun slotClicked(slot: Slot, slotId: Int, mouseButton: Int, type: ClickType) {
		delaySlotClick(slot, mouseButton)
	}

    protected fun createNewGui(block: (Slot) -> ItemStack) {
		GuiEvents.OPEN.invoker().onOpen(name, containerId++)
        guiInventorySlots.forEach { it.setSlot(block(it)) }
    }

    protected fun Slot.setSlot(stack: ItemStack) {
		GuiEvents.SLOT_UPDATE_AFTER.invoker().onSlotUpdateAfter(containerId, index, stack)
        setByPlayer(stack)
    }

    protected fun playTermSimSound() {
		MC.runOnThread {
			MC.player?.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1f)
		}
    }
}