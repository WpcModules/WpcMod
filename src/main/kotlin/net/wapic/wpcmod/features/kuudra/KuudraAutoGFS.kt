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
    private val maxStackSize: Int get() = 16

    init {
        ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
    }

    private fun onMessageReceived(text: Text, actionBar: Boolean){
        if(actionBar) return

        if(text.string == "[NPC] Elle: Okay adventurers, I will go and fish up Kuudra!" && config.autoGfs && Utils.getLocation() == Island.KUUDRA){
            val player = MinecraftClient.getInstance().player ?: return
            val slotId = player.inventory.getSlotWithStack(Items.ENDER_PEARL.defaultStack).takeIf { it != -1 } ?: return
            val stackSize = player.inventory.getStack(slotId).count

            Utils.addToCommandQueue("gfs ENDER_PEARL ${maxStackSize - stackSize}")
        }
    }
}