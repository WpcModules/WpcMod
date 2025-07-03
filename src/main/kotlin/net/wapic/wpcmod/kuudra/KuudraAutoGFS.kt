package net.wapic.wpcmod.kuudra

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils

object KuudraAutoGFS {

    val client: MinecraftClient = MinecraftClient.getInstance()
    val config = WpcMod.config.instance.kuudraConfig

    fun init() {
        ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
    }

    fun onMessageReceived(text: Text, actionBar: Boolean){
        if(actionBar) return

        if(text.string == "[NPC] Elle: Okay adventurers, I will go and fish up Kuudra!" && config.autoGfs && Utils.getLocation() == Island.KUUDRA){
            val player = client.player ?: return
            val slot = player.inventory.getSlotWithStack(Items.ENDER_PEARL.defaultStack)
            val amount = if(slot == -1) return else player.inventory.getStack(slot).count

            Utils.addToCommandQueue("gfs ENDER_PEARL ${16 - amount}")
        }
    }
}