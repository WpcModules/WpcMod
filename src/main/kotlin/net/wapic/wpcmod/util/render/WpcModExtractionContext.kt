package net.wapic.wpcmod.util.render

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.config.components.EspConfig
import net.wapic.wpcmod.util.render.state.*
import org.joml.Vector3f

class WpcModExtractionContext {

	private val renderStates: MutableList<RenderState>
	val level: ClientLevel
	val camera: Camera
	val partialTicks: Float

	constructor(renderStates: MutableList<RenderState>, level: ClientLevel, camera: Camera, partialTicks: Float) {
		this.renderStates = renderStates
		this.level = level
		this.camera = camera
		this.partialTicks = partialTicks
	}

	fun entityESP(entity: Entity, state: EntityState) {
		val width = state.width ?: entity.bbWidth
		val height = state.height ?: entity.bbHeight
		val yOffset = state.yOffset ?: 0f
		val cameraPos = camera.position().toVector3f().add(camera.forwardVector())
		val pos = entity.getPosition(partialTicks).toVector3f().sub(width / 2, -yOffset, width / 2)
		renderStates.add(EspRenderState(state.config, pos, width, height, cameraPos))
	}

	fun blockESP(blockPos: BlockPos, config: EspConfig) {
		val cameraPos = camera.position().toVector3f().add(camera.forwardVector())
		val pos = Vector3f(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat())
		renderStates.add(EspRenderState(config, pos, 1f, 1f, cameraPos))
	}

	fun text(text: String, pos: Vec3, color: ChromaColour = ChromaColour.WHITE, scale: Float = 1f, shadow: Boolean = true, background: Boolean = true) {
		renderStates.add(TextRenderState(text, pos.toVector3f(), color, scale, shadow, background, camera.position().toVector3f(), camera.rotation()))
	}

	fun aabb(aabb: AABB, color: ChromaColour, filled: Boolean = false) {
		val minPos = aabb.minPosition.toVector3f()
		val maxPos = aabb.maxPosition.toVector3f()
		val state = if (filled) {
			FilledBoxRenderState(minPos.x, minPos.y, minPos.z, maxPos.x, maxPos.y, maxPos.z, color)
		} else {
			BoxRenderState(minPos.x, minPos.y, minPos.z, maxPos.x, maxPos.y, maxPos.z, color, 2f)
		}
		renderStates.add(state)
	}

	fun filledAABB(aabb: AABB, color: ChromaColour, outlineColor: ChromaColour) {
		val minPos = aabb.minPosition.toVector3f()
		val maxPos = aabb.maxPosition.toVector3f()
		val filledBoxState = FilledBoxRenderState(minPos.x, minPos.y, minPos.z, maxPos.x, maxPos.y, maxPos.z, color)
		val outlineBoxState = BoxRenderState(minPos.x, minPos.y, minPos.z, maxPos.x, maxPos.y, maxPos.z, outlineColor, 2f)
		renderStates.addAll(listOf(filledBoxState, outlineBoxState))
	}

	fun blockPos(blockPos: BlockPos, color: ChromaColour, filled: Boolean = false) {
		aabb(AABB(blockPos), color, filled)
	}

	fun box(pos: Vec3, width: Float, height: Float, color: ChromaColour, filled: Boolean = false) {
		aabb(AABB(pos, pos.add(width.toDouble(), height.toDouble(), width.toDouble())), color, filled)
	}

	fun filledBox(pos: Vec3, width: Float, height: Float, color: ChromaColour, outlineColor: ChromaColour) {
		filledAABB(AABB(pos, pos.add(width.toDouble(), height.toDouble(), width.toDouble())), color, outlineColor)
	}

	fun line(pos1: Vec3, pos2: Vec3, color: ChromaColour, lineWidth: Float) {
		renderStates.add(LineRenderState(pos1.toVector3f(), pos2.toVector3f(), color, lineWidth))
	}

	fun tracer(pos: Vec3, color: ChromaColour, lineWidth: Float) {
		val pos = pos.toVector3f()
		val cameraPos = camera.position().toVector3f().add(camera.forwardVector())
		renderStates.add(LineRenderState(pos, cameraPos, color, lineWidth))
	}

	fun tracer(pos: BlockPos, color: ChromaColour, lineWidth: Float) = tracer(Vec3(pos), color, lineWidth)
}