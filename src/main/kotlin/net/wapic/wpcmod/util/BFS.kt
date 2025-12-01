package net.wapic.wpcmod.util

import net.minecraft.block.Block
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import java.util.*

object BFSProcessor {
	private var world: ClientWorld? = null
	private var pos: BlockPos? = null
	private var targetBlock: Block? = null
	private var radius: Int = 0
	private var maxSteps: Int = 0

	private val queue: Queue<BlockPos> = ArrayDeque()
	private val visited = mutableSetOf<BlockPos>()
	private val exposed = mutableSetOf<BlockPos>()
	private var done = false

	fun start(world: ClientWorld, pos: BlockPos, targetBlock: Block, radius: Int, maxSteps: Int) {
		reset()

		this.world = world
		this.pos = pos
		this.targetBlock = targetBlock
		this.radius = radius
		this.maxSteps = maxSteps

		done = false
		queue.add(pos)
	}

	fun tick() {
		if (done) return

		var steps = 0

		while (queue.isNotEmpty() && steps < maxSteps) {
			val current = queue.poll()
			visited.add(current)
			steps++

			for (dir in Direction.entries) {
				val neighbor = current.offset(dir)
				if (neighbor in visited) continue
				if (neighbor.getManhattanDistance(pos) > radius) continue

				world?.let {
					val state = it.getBlockState(neighbor)
					if (state.isAir) {
						queue.add(neighbor)
						visited.add(neighbor)
					} else if (state.isOf(targetBlock)) {
						exposed.add(neighbor.toImmutable())
					}
				}
			}
		}

		if (queue.isEmpty()) {
			done = true
		}
	}

	fun reset() {
		world = null
		pos = null
		targetBlock = null
		radius = 0
		maxSteps = 0

		queue.clear()
		visited.clear()
		exposed.clear()

		done = true
	}

	fun blocks() = exposed
	fun done() = done
}