package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Util
import net.wapic.wpcmod.WpcMod
import kotlin.jvm.optionals.getOrDefault

object Utils {
    private const val MIN_DELAY: Long = 500
    private val commandQueue = mutableListOf<String>()
    private var lastCommand: Long = 0

    private var location: Island? = null

    fun init() {
        ClientTickEvents.END_CLIENT_TICK.register(::onTick)
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket::class.java)
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket::class.java, ::onHypixelLocationPacket)
    }

    fun getLocation(): Island? {
        return location
    }

    fun addToCommandQueue(command: String) {
        val time = Util.getMeasuringTimeMs() - lastCommand
        if(time < MIN_DELAY || !commandQueue.isEmpty()){
            commandQueue.add(command)
            return
        }
        runCommand(command)
    }
    private fun runCommand(command: String) {
        MinecraftClient.getInstance().networkHandler?.sendCommand(if(command[0] == '/') command.removePrefix("/") else command)
        lastCommand = Util.getMeasuringTimeMs()
    }

    private fun onTick(client: MinecraftClient){
        val iterator = commandQueue.iterator()
        if(Util.getMeasuringTimeMs() - lastCommand > MIN_DELAY){
            while(iterator.hasNext()) {
                runCommand(iterator.next())
                iterator.remove()
                return
            }
        }
    }

    private fun onHypixelLocationPacket(packet: ClientboundLocationPacket){
        if(packet.map.isPresent){
            location = Island.fromDisplayName(packet.map.get())
            WpcMod.logger.info("Map set to: $location")
        }
    }
}