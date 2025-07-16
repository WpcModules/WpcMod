package net.wapic.wpcmod.features.kuudra

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils

class KuudraAutoGFS {

    private val config get() = WpcMod.config.kuudraConfig

    init {
        ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
    }

    private fun onMessageReceived(text: Text, actionBar: Boolean) {
        if(actionBar || Utils.getLocation() == Island.KUUDRA) return

        if(config.autoGfs && text.string.equals(KUUDRA_START_MESSAGE)) {
            val player = MinecraftClient.getInstance().player ?: return
            val slotId = player.inventory.getSlotWithStack(Items.ENDER_PEARL.defaultStack)
            val stackSize = if(slotId == -1) 0 else player.inventory.getStack(slotId).count

            Utils.addToCommandQueue("gfs ENDER_PEARL ${MAX_STACK_SIZE - stackSize}")
        }
    }

    companion object {
        private const val MAX_STACK_SIZE: Int = 16
        private const val KUUDRA_START_MESSAGE: String = "[NPC] Elle: Okay adventurers, I will go and fish up Kuudra!"
    }
}