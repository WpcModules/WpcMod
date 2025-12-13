package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import com.google.common.primitives.Shorts
import com.google.common.primitives.SignedBytes
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.HashedStack
import net.minecraft.network.PacketListener
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.termsim.TermSimGUI
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import org.lwjgl.glfw.GLFW
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.concurrent.CopyOnWriteArrayList

open class TerminalHandler(val type: TerminalTypes) {
    val solution: CopyOnWriteArrayList<Int> = CopyOnWriteArrayList()
    val items: Array<ItemStack?> = arrayOfNulls(type.windowSize)
    val timeOpened = System.currentTimeMillis()
    var isClicked = false
	var containerId = -1

    fun onPacketReceive(packet: Packet<out PacketListener>) = with (packet) {
        when (this) {
            is ClientboundContainerSetSlotPacket -> {
                if (slot !in 0 until type.windowSize) return@with
                items[slot] = item
                if (handleSlotUpdate(this)) DungeonEvents.TERMINAL_UPDATED.invoker().onOpen(this@TerminalHandler)
            }

            is ClientboundOpenScreenPacket -> {
				this@TerminalHandler.containerId = containerId
                isClicked = false
                items.fill(null)
            }
        }
    }

    init {
		PacketEvents.RECEIVE.register(::onPacketReceive)
    }

    open fun handleSlotUpdate(packet: ClientboundContainerSetSlotPacket): Boolean = false

    open fun simulateClick(slotIndex: Int, clickType: Int) {}

	open fun click(slotIndex: Int, button: Int, simulateClick: Boolean = true) {
		val screenHandler = (MC.screen as? ContainerScreen)?.menu ?: return
        if (simulateClick) simulateClick(slotIndex, button)
        isClicked = true

		if (MC.screen is TermSimGUI) {
			PacketEvents.SEND_BEFORE.invoker().onPacketSendBefore(
				ServerboundContainerClickPacket(
					screenHandler.containerId,
					MC.player?.containerMenu?.stateId ?: 0,
					Shorts.checkedCast(slotIndex.toLong()), SignedBytes.checkedCast(button.toLong()),
					if (button == GLFW.GLFW_MOUSE_BUTTON_3) ClickType.CLONE else ClickType.PICKUP,
					Int2ObjectOpenHashMap(), HashedStack.EMPTY
				),
				CallbackInfo("TermSimClick", true)
			)
			return
		}
		MC.interactionManager?.handleInventoryMouseClick(
			screenHandler.containerId,
			slotIndex,
			button,
			if (button == GLFW.GLFW_MOUSE_BUTTON_3) ClickType.CLONE else ClickType.PICKUP,
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