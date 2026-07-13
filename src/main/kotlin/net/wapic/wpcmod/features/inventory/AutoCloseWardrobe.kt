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

	private val wardrobeTitle = Regex("^\\((?<page>\\d)/\\d\\) (Armor|Equipment) Sets$")
	private val loudoutsTitle = Regex("^\\((?<page>\\d)/\\d\\) Loadouts$")

	fun init() {
		PacketEvents.SEND_AFTER.register(::onPacketSent)
	}

	private fun onPacketSent(packet: Packet<out PacketListener>, callbackInfo: CallbackInfo) {
		if (packet !is ServerboundContainerClickPacket || !config.autoCloseWardrobe) return

		val inLoadouts = MC.screen?.title?.string?.matches(loudoutsTitle) == true
		val isValidLoadoutSlot = packet.slotNum % 9 in 5..7 && packet.slotNum / 9 in 1..4

		val inWardrobe = MC.screen?.title?.string?.matches(wardrobeTitle) == true
		val isValidWardrobeSlot = packet.slotNum in 36..44

		val shouldClose = (inLoadouts && isValidLoadoutSlot) || (inWardrobe && isValidWardrobeSlot)

		if (shouldClose) {
			MC.runOnThread { MC.screen?.onClose() }
		}
	}
}