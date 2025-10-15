package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.ItemStack
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.screen.slot.SlotActionType
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import java.util.concurrent.CopyOnWriteArrayList

open class TerminalHandler(val type: TerminalTypes) {
    val solution: CopyOnWriteArrayList<Int> = CopyOnWriteArrayList()
    val items: Array<ItemStack?> = arrayOfNulls(type.windowSize)
    val timeOpened = System.currentTimeMillis()
    var isClicked = false

    fun onPacketReceive(packet: Packet<out PacketListener>) = with (packet) {
        when (this) {
            is ScreenHandlerSlotUpdateS2CPacket -> {
                if (slot !in 0 until type.windowSize) return@with
                items[slot] = stack
                if (handleSlotUpdate(this)) DungeonEvents.TERMINAL_UPDATED.invoker().onOpen(this@TerminalHandler)
            }
            is OpenScreenS2CPacket -> {
                isClicked = false
                items.fill(null)
            }
        }
    }

    init {
		PacketEvents.RECEIVE.register(::onPacketReceive)
    }

    open fun handleSlotUpdate(packet: ScreenHandlerSlotUpdateS2CPacket): Boolean = false

    open fun simulateClick(slotIndex: Int, clickType: Int) {}

    fun click(slotIndex: Int, button: Int, simulateClick: Boolean = true) {
        if (simulateClick) simulateClick(slotIndex, button)
        isClicked = true
        val screenHandler = (MC.screen as? GenericContainerScreen)?.screenHandler ?: return
        MC.interactionManager?.clickSlot(screenHandler.syncId, slotIndex, button, SlotActionType.PICKUP, MC.player)
    //PlayerUtils.windowClick(slotIndex, clickType)
    }

    fun canClick(slotIndex: Int, button: Int, needed: Int = solution.count { it == slotIndex }): Boolean = when {
        type == TerminalTypes.MELODY -> slotIndex.equalsOneOf(16, 25, 34, 43)
        slotIndex !in solution -> false
        type == TerminalTypes.NUMBERS && slotIndex != solution.firstOrNull() -> false
        type == TerminalTypes.RUBIX && ((needed < 3 && button == 1) || (needed.equalsOneOf(3, 4) && button != 1)) -> false
        else -> true
    }
}