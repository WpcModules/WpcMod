package net.wapic.wpcmod.features.inventory

import net.minecraft.network.PacketListener
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.util.MC
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object AutoCloseWardrobe {
	private val config get() = WpcMod.config.inventory

	private val wardrobeTitle = Regex("^Wardrobe \\((?<page>\\d)/\\d\\)$")
	private val slotEquippedRegex = Regex("^Slot \\d+: Equipped$")
	private var lastClickedSlot: Int? = null

	fun init() {
		GuiEvents.SLOT_UPDATE_BEFORE.register(::onSlotUpdate)
		PacketEvents.SEND.register(::onPacketSent)
		GuiEvents.CLOSE.register { lastClickedSlot = null }
	}

	private fun onSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack) {
		if (!config.autoCloseWardrobe || slotId != lastClickedSlot) return
		val inWardrobe = MC.screen?.title?.string?.matches(wardrobeTitle) ?: return
		val itemMatches = itemStack.hoverName.string.matches(slotEquippedRegex)

		if (inWardrobe && itemMatches) {
			MC.runOnThread { MC.screen?.onClose() }
		}
	}

	private fun onPacketSent(packet: Packet<out PacketListener>, callbackInfo: CallbackInfo) {
		if (packet !is ServerboundContainerClickPacket) return

		if (MC.screen?.title?.string?.matches(wardrobeTitle) == true)
			lastClickedSlot = packet.slotNum.toInt()
	}
}