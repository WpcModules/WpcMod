package net.wapic.wpcmod.features.dungeons.floor7.termsim

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.player.PlayerEquipment
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalHandler
import net.wapic.wpcmod.util.MC
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

open class TermSimGUI(
    val name: String,
    val size: Int,
    private val inv: SimpleInventory = SimpleInventory(size)
) : GenericContainerScreen(
    GenericContainerScreenHandler(
        if (size <= 9) ScreenHandlerType.GENERIC_9X1
        else if (size <= 18) ScreenHandlerType.GENERIC_9X2
        else if (size <= 27) ScreenHandlerType.GENERIC_9X3
        else if (size <= 36) ScreenHandlerType.GENERIC_9X4
        else if (size <= 45) ScreenHandlerType.GENERIC_9X5
        else ScreenHandlerType.GENERIC_9X6,
        0, PlayerInventory(MC.player, PlayerEquipment(MC.player)), inv, size / 9
    ),
    PlayerInventory(MC.player, PlayerEquipment(MC.player)),
    Text.literal(name)
) {
    val blackPane = ItemStack(Items.BLACK_STAINED_GLASS_PANE).apply { set(DataComponentTypes.CUSTOM_NAME, Text.literal("")) }
    val guiInventorySlots get() = handler?.slots?.subList(0, size) ?: emptyList()
    private var doesAcceptClick = true
    protected var ping = 0L

	init {
		PacketEvents.SEND.register(::onPacketSend)
		DungeonEvents.TERMINAL_SOLVED.register(::onTerminalSolved)
	}

    open fun create() {
        guiInventorySlots.forEach { it.setSlot(blackPane) }
    }

    fun open(terminalPing: Long = 0L) {
		MC.instance.send {
			MC.screen = this
			create()
			ping = terminalPing
		}
    }

    fun onTerminalSolved(terminalHandler: TerminalHandler) {
        if (MC.screen !== this) return
		PacketEvents.RECEIVE.invoker().onPacketReceive(CloseScreenS2CPacket(-2))
        StartGUI.open(ping)
    }

    open fun slotClick(slot: Slot, button: Int) {}

    override fun close() {
        doesAcceptClick = true
        super.close()
    }

    override fun init() {
        super.init()
    }

    fun onPacketSend(packet: Packet<out PacketListener>, callbackInfo: CallbackInfo) {
        val packet = packet as? ClickSlotC2SPacket ?: return
        if (MC.screen !== this) return
        delaySlotClick(guiInventorySlots.getOrNull(packet.slot.toInt()) ?: return, packet.button.toInt())
        callbackInfo.cancel()
    }

    fun onPacketReceive() {
//        val packet = event.packet as? ScreenHandlerSlotUpdateS2CPacket ?: return
//        if (OdinMain.mc.currentScreen !== this || packet.func_149175_c() == -2 || event.packet.func_149173_d() !in 0 until size) return
//        packet.func_149174_e()?.let { mc.thePlayer?.inventoryContainer?.putStackInSlot(packet.func_149173_d(), it) }
//        event.isCanceled = true
    }

    private fun delaySlotClick(slot: Slot, button: Int) {
        if (MC.screen == StartGUI) return slotClick(slot, button)
        if (!doesAcceptClick || slot.inventory != inv || slot.stack?.item == Items.BLACK_STAINED_GLASS_PANE) return
        doesAcceptClick = false

		if (MC.screen != this) return
		doesAcceptClick = true
		slotClick(slot, button)
    }

    override fun onMouseClick(slot: Slot?, slotId: Int, button: Int, actionType: SlotActionType?) {
        slot?.let { delaySlotClick(it, slotId) }
    }

    protected fun createNewGui(block: (Slot) -> ItemStack) {
		PacketEvents.RECEIVE.invoker().onPacketReceive(OpenScreenS2CPacket(0, ScreenHandlerType.GENERIC_9X3, Text.literal(name)))
        guiInventorySlots.forEach { it.setSlot(block(it)) }
    }

    protected fun Slot.setSlot(stack: ItemStack) {
		PacketEvents.RECEIVE.invoker().onPacketReceive(ScreenHandlerSlotUpdateS2CPacket(-2, 0, id, stack))
        setStack(stack)
    }

    protected fun playTermSimSound() {
		MC.player?.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
    }
}