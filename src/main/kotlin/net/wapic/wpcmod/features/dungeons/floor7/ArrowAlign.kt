package net.wapic.wpcmod.features.dungeons.floor7

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.item.Items
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.mixin.accessors.PlayerInteractEntityC2SPacketAccessor
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.DungeonUtils.F7Phase
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.render.WorldRenderContext
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object ArrowAlign {
	private val config get() = WpcMod.config.dungeon.floor7.arrowAlign

    private val frameGridCorner = BlockPos(-2, 120, 75)
    private val recentClickTimestamps = mutableMapOf<Int, Long>()
    private val clicksRemaining = mutableMapOf<Int, Int>()
    private var currentFrameRotations: List<Int>? = null
    private var targetSolution: List<Int>? = null

	fun init() {
		PacketEvents.SEND.register(::onPacketSend)
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		WorldRenderEvent.EVENT.register(::onRenderWorld)
    }

	fun onTick(client: MinecraftClient) {
		if (DungeonUtils.getF7Phase() != F7Phase.GOLDOR || !config.enabled) return

		clicksRemaining.clear()
		if ((MC.player?.entityPos?.distanceTo(Vec3d(0.0, 120.0, 77.0)) ?: return) > 200) {
			currentFrameRotations = null
			targetSolution = null
			return
		}

		currentFrameRotations = getFrames()

		possibleSolutions.forEach { arr ->
			for (i in arr.indices) {
				if ((arr[i] == -1 || currentFrameRotations?.get(i) == -1) && arr[i] != currentFrameRotations?.get(i)) return@forEach
			}

			targetSolution = arr

			for (i in arr.indices) {
				clicksRemaining[i] = calculateClicksNeeded(currentFrameRotations?.get(i) ?: return@forEach, arr[i]).takeIf { it != 0 } ?: continue
			}
		}
	}

	fun onPacketSend(packet: Packet<out PacketListener>, ci: CallbackInfo) {
		if (DungeonUtils.getF7Phase() != F7Phase.GOLDOR || !config.enabled) return

        val packet = packet as? PlayerInteractEntityC2SPacket ?: return
		packet.handle(object : PlayerInteractEntityC2SPacket.Handler {
			override fun interact(hand: Hand?) {
				val entity =
					MC.world?.getEntityById((packet as PlayerInteractEntityC2SPacketAccessor).entityId) as? ItemFrameEntity
						?: return
				if (entity.heldItemStack?.item != Items.ARROW) return

				val frameIndex = ((entity.blockY - frameGridCorner.y) + (entity.blockZ - frameGridCorner.z) * 5)
				if (entity.blockX != frameGridCorner.x || currentFrameRotations?.get(frameIndex) == -1 || frameIndex !in 0..24) return

				if (!clicksRemaining.containsKey(frameIndex) && MC.player?.isSneaking == config.invertSneak && config.blockWrongClick) {
					ci.cancel()
					return
				}

				recentClickTimestamps[frameIndex] = System.currentTimeMillis()
				currentFrameRotations =
					currentFrameRotations?.toMutableList()?.apply { this[frameIndex] = (this[frameIndex] + 1) % 8 }

				if (calculateClicksNeeded(
						currentFrameRotations?.get(frameIndex) ?: return,
						targetSolution?.get(frameIndex) ?: return
					) == 0
				) clicksRemaining.remove(frameIndex)
			}

			override fun interactAt(hand: Hand?, pos: Vec3d?) {}
			override fun attack() {}
		})
	}

    fun onRenderWorld(worldRenderContext: WorldRenderContext) {
        if (clicksRemaining.isEmpty() || DungeonUtils.getF7Phase() != F7Phase.GOLDOR || !config.enabled) return
		worldRenderContext.profiler.push("ArrowAlign")

        clicksRemaining.forEach { (index, clickNeeded) ->
            val colorCode = when {
                clickNeeded == 0 -> return@forEach
                clickNeeded < 3 -> 'a'
                clickNeeded < 5 -> '6'
                else -> 'c'
            }

			worldRenderContext.drawText(
                Text.of("§$colorCode$clickNeeded").asOrderedText(),
                getFramePositionFromIndex(index).toCenterPos().add(Vec3d(-0.3, 0.0, 0.1)),
                1f,

				false
            )
        }
		worldRenderContext.profiler.pop()
    }

    private fun getFrames(): List<Int> {
        val itemFrames = MC.world?.entities?.mapNotNull {
            if (it is ItemFrameEntity && it.heldItemStack?.item?.asItem() == Items.ARROW) it else null
        }?.takeIf { it.isNotEmpty() } ?: return List(25) { -1 }

        return (0..24).map { index ->
            if (recentClickTimestamps[index]?.let { System.currentTimeMillis() - it < 1000 } == true && currentFrameRotations != null)
                currentFrameRotations?.get(index) ?: -1
            else
                itemFrames.find { it.blockPos == getFramePositionFromIndex(index) }?.rotation ?: -1
        }
    }

    private fun getFramePositionFromIndex(index: Int): BlockPos =
        frameGridCorner.add(0, index % 5, index / 5)

    private fun calculateClicksNeeded(currentRotation: Int, targetRotation: Int): Int =
        (8 - currentRotation + targetRotation) % 8

    private val possibleSolutions = listOf(
        listOf(7, 7, -1, -1, -1, 1, -1, -1, -1, -1, 1, 3, 3, 3, 3, -1, -1, -1, -1, 1, -1, -1, -1, 7, 1),
        listOf(-1, -1, 7, 7, 5, -1, 7, 1, -1, 5, -1, -1, -1, -1, -1, -1, 7, 5, -1, 1, -1, -1, 7, 7, 1),
        listOf(7, 7, -1, -1, -1, 1, -1, -1, -1, -1, 1, 3, -1, 7, 5, -1, -1, -1, -1, 5, -1, -1, -1, 3, 3),
        listOf(5, 3, 3, 3, -1, 5, -1, -1, -1, -1, 7, 7, -1, -1, -1, 1, -1, -1, -1, -1, 1, 3, 3, 3, -1),
        listOf(5, 3, 3, 3, 3, 5, -1, -1, -1, 1, 7, 7, -1, -1, 1, -1, -1, -1, -1, 1, -1, 7, 7, 7, 1),
        listOf(7, 7, 7, 7, -1, 1, -1, -1, -1, -1, 1, 3, 3, 3, 3, -1, -1, -1, -1, 1, -1, 7, 7, 7, 1),
        listOf(-1, -1, -1, -1, -1, 1, -1, 1, -1, 1, 1, -1, 1, -1, 1, 1, -1, 1, -1, 1, -1, -1, -1, -1, -1),
        listOf(-1, -1, -1, -1, -1, 1, 3, 3, 3, 3, -1, -1, -1, -1, 1, 7, 7, 7, 7, 1, -1, -1, -1, -1, -1),
        listOf(-1, -1, -1, -1, -1, -1, 1, -1, 1, -1, 7, 1, 7, 1, 3, 1, -1, 1, -1, 1, -1, -1, -1, -1, -1)
    )
}