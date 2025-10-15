package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Util
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.WorldChunk
import net.wapic.wpcmod.WpcMod
import java.util.Locale
import kotlin.math.max

object Utils {

	private const val MIN_DELAY: Long = 500
	private val commandQueue = mutableListOf<String>()
	private var lastCommand: Long = 0

	private var location: Island? = null

	fun Any?.equalsOneOf(vararg other: Any): Boolean = other.any { this == it }
	fun Number.toFixed(decimals: Int = 2): String = "%.${decimals}f".format(Locale.ENGLISH, this)

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register { onTick() }
		HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket::class.java)
		HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket::class.java, ::onHypixelLocationPacket)
	}

	fun addToCommandQueue(command: String) {
		if (Util.getMeasuringTimeMs() - lastCommand < MIN_DELAY || commandQueue.isNotEmpty()) {
			commandQueue.add(command)
			return
		}
		runCommand(command)
	}

	fun runCommand(command: String) {
		MinecraftClient.getInstance().networkHandler?.sendCommand(command.removePrefix("/"))
	}

	private fun onTick() {
		if (commandQueue.isEmpty()) return

		if (Util.getMeasuringTimeMs() - lastCommand > MIN_DELAY) {
			runCommand(commandQueue.first())
			lastCommand = Util.getMeasuringTimeMs()
			commandQueue.removeFirst()
		}
	}

	fun getLocation(): Island? {
		return location
	}

	private fun onHypixelLocationPacket(packet: ClientboundLocationPacket) {
		if (packet.map.isPresent) {
			location = Island.fromDisplayName(packet.map.get())
			WpcMod.logger.info("Map set to: $location")
		}
	}

	fun getLoadedBlockEntities(): List<BlockEntity> {
		val blockEntities = mutableListOf<BlockEntity>()
		val chunks = getLoadedChunks()
		for(chunk in chunks) {
			chunk?.blockEntities?.values?.forEach { blockEntity -> blockEntities.add(blockEntity) }
		}
		return blockEntities
	}

	fun getLoadedChunks(): List<WorldChunk?> {
		val mc = MinecraftClient.getInstance()
		val radius = max(2, mc.options.clampedViewDistance) + 3

		val chunks = mutableSetOf<ChunkPos>()

		mc.player?.let {
			val center = it.chunkPos

			for (x in -radius..radius) {
				for (z in -radius..radius) {
					chunks.add(ChunkPos(center.x + x, center.z + z))
				}
			}
		}

		return chunks.filter { chunk -> mc.world?.isChunkLoaded(chunk.x, chunk.z) ?: false }
			.map { chunk -> mc.world?.getChunk(chunk.x,chunk.z) }
			.filter { chunk -> chunk?.isEmpty == false }
	}

}