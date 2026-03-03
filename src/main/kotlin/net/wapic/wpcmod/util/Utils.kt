package net.wapic.wpcmod.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.minecraft.Util
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.chunk.EmptyLevelChunk
import net.minecraft.world.level.chunk.LevelChunk
import net.wapic.wpcmod.WpcMod
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.max

object Utils {

	private const val MIN_DELAY: Long = 500
	private val commandQueue = ArrayDeque<String>()
	private var lastCommand: Long = 0

	private var location: Island? = null

	fun init() {

		ClientTickEvents.END_CLIENT_TICK.register { onTick() }
		HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket::class.java)
		HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket::class.java, ::onHypixelLocationPacket)
	}

	fun Any?.equalsOneOf(vararg other: Any): Boolean = other.any { this == it }
	fun modIdentifier(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(WpcMod.MOD_ID, path)
	fun Number.toFixed(decimals: Int = 2): String = "%.${decimals}f".format(Locale.ENGLISH, this)

	fun copyToClipboard(string: String) {
		MC.keyboard.clipboard = string
	}

	fun addToCommandQueue(command: String) {
		if (Util.getMillis() - lastCommand < MIN_DELAY || commandQueue.isNotEmpty()) {
			commandQueue.add(command)
			return
		}
		runCommand(command)
	}

	fun runCommand(command: String) {
		MC.connection?.sendCommand(command.removePrefix("/"))
	}

	private fun onTick() {
		if (commandQueue.isEmpty()) return

		if (Util.getMillis() - lastCommand > MIN_DELAY) {
			runCommand(commandQueue.pollFirst())
			lastCommand = Util.getMillis()
			commandQueue.removeFirst()
		}
	}

	fun getLocation(): Island? {
		return location
	}

	private fun onHypixelLocationPacket(packet: ClientboundLocationPacket) {
		location = Island.fromDisplayName(packet.map.getOrNull())
		WpcMod.logger.info("Location set to: $location")
	}

	fun getLoadedBlockEntities(): List<BlockEntity> {
		val blockEntities = mutableListOf<BlockEntity>()
		val chunks = getLoadedChunks()
		for(chunk in chunks) {
			chunk?.blockEntities?.values?.forEach(blockEntities::add)
		}
		return blockEntities
	}

	fun getLoadedChunks(): List<LevelChunk?> {
		val radius = max(2, MC.options.effectiveRenderDistance) + 3
		val chunks = mutableSetOf<ChunkPos>()

		MC.player?.let {
			val center = it.chunkPosition()

			for (x in -radius..radius) {
				for (z in -radius..radius) {
					chunks.add(ChunkPos(center.x + x, center.z + z))
				}
			}
		}

		return chunks.filter { chunk -> MC.level?.getChunk(chunk.x, chunk.z) !is EmptyLevelChunk }
			.map { chunk -> MC.level?.getChunk(chunk.x, chunk.z) }
	}

}