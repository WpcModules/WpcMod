package net.wapic.wpcmod.features.inventory

import net.minecraft.network.PacketListener
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.util.MC
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object AutoCloseWardrobe {
	private val config get() = WpcMod.config.inventory

	private val wardrobeTitle = Regex("^Wardrobe \\((?<page>\\d)/\\d\\)$")
	private val slotEquippedRegex = Regex("^Slot \\d+: Equipped$")
	private var lastClickedSlot: Int? = null

	fun init() {
		PacketEvents.SEND_AFTER.register(::onPacketSent)
	}

	private fun onPacketSent(packet: Packet<out PacketListener>, callbackInfo: CallbackInfo) {
		if (packet !is ServerboundContainerClickPacket) return

		if (MC.screen?.title?.string?.matches(wardrobeTitle) == true && packet.slotNum in 36..44)
			MC.runOnThread { MC.screen?.onClose() }
	}
}