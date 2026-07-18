package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.core.Direction
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.unaryMinus
import org.joml.Vector3f
import org.joml.minus

class WorldRenderContext(
	val matrixStack: PoseStack,
	val camera: CameraRenderState,
	val profiler: ProfilerFiller,
) {

	fun drawText(text: FormattedCharSequence, pos: Vec3, scale: Float, depth: Boolean) {
		matrixStack.pushPose()
		val scale = scale * 0.025f
		val matrix = matrixStack.last().pose()

		matrixStack.mulPose(matrix)
		matrixStack.translate(pos)
		matrixStack.translate(-camera.pos)
		matrixStack.mulPose(camera.orientation)
		matrixStack.scale(scale, -scale, scale)

		MC.font.prepareText(text, -MC.font.width(text) / 2f, 0f, -1, true, true, -1)

		matrixStack.popPose()
	}

	fun drawFilledBoxWithOutline(
		aabb: AABB,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		outlineColor: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		drawFilledBox(
			aabb.minX.toFloat(), aabb.minY.toFloat(), aabb.minZ.toFloat(),
			aabb.maxX.toFloat(), aabb.maxY.toFloat(), aabb.maxZ.toFloat(),
			color
		)
		drawBox(
			aabb.minX.toFloat(), aabb.minY.toFloat(), aabb.minZ.toFloat(),
			aabb.maxX.toFloat(), aabb.maxY.toFloat(), aabb.maxZ.toFloat(),
			outlineColor,
			lineWidth
		)
	}

	fun drawBoundingBox(
		pos: Vec3, width: Float, height: Float,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		val width = width / 2
		val height = height / 2
		val pos = pos.toVector3f()
		drawBox(
			pos.x - width, pos.y - height, pos.z - width,
			pos.x + width, pos.y + height, pos.z + width,
			color,
			lineWidth,
		)
	}

	fun drawBoundingBox(
		aabb: AABB,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
	}

	fun drawTracer(
		pos: Vec3,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		val pos = pos.toVector3f()
		drawTracer(pos.x, pos.y, pos.z, color, lineWidth)
	}

	fun drawTracer(
		x: Float, y: Float, z: Float,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		val viewBobbing = MC.options.bobView().get()
		MC.options.bobView().set(false)

		val forward = camera.orientation.transform(Vector3f(0f, 0f, -1f))

		val cameraPos = camera.pos.toVector3f().add(forward)
		val targetPos = Vector3f(x, y, z)

		drawLine(cameraPos, targetPos, color, lineWidth)

		MC.options.bobView().set(viewBobbing)
	}

	fun drawFilledBox(
		minX: Float, minY: Float, minZ: Float,
		maxX: Float, maxY: Float, maxZ: Float,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
	) {
		draw(WpcModRenderPipelines.QUADS) { consumer ->
			val color = color.getEffectiveColourRGB()
			val pose = matrixStack.last()

			for (dir in Direction.entries) {
				buildFace(consumer, pose, minX, minY, minZ, maxX, maxY, maxZ, color, dir)
			}
		}
	}

	fun drawBox(
		minX: Float, minY: Float, minZ: Float,
		maxX: Float, maxY: Float, maxZ: Float,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		drawLine(Vector3f(minX, minY, minZ), Vector3f(minX, maxY, minZ), color, lineWidth)
		drawLine(Vector3f(maxX, minY, minZ), Vector3f(maxX, maxY, minZ), color, lineWidth)
		drawLine(Vector3f(minX, minY, minZ), Vector3f(minX, minY, maxZ), color, lineWidth)
		drawLine(Vector3f(minX, maxY, minZ), Vector3f(minX, maxY, maxZ), color, lineWidth)

		drawLine(Vector3f(minX, minY, maxZ), Vector3f(minX, maxY, maxZ), color, lineWidth)
		drawLine(Vector3f(maxX, minY, maxZ), Vector3f(maxX, maxY, maxZ), color, lineWidth)
		drawLine(Vector3f(maxX, minY, minZ), Vector3f(maxX, minY, maxZ), color, lineWidth)
		drawLine(Vector3f(maxX, maxY, minZ), Vector3f(maxX, maxY, maxZ), color, lineWidth)

		drawLine(Vector3f(minX, minY, minZ), Vector3f(maxX, minY, minZ), color, lineWidth)
		drawLine(Vector3f(minX, maxY, minZ), Vector3f(maxX, maxY, minZ), color, lineWidth)
		drawLine(Vector3f(minX, minY, maxZ), Vector3f(maxX, minY, maxZ), color, lineWidth)
		drawLine(Vector3f(minX, maxY, maxZ), Vector3f(maxX, maxY, maxZ), color, lineWidth)
	}

	private fun buildFace(consumer: VertexConsumer, pose: PoseStack.Pose, minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float, color: Int, face: Direction, lineWidth: Float = 2f) {
		when (face) {
			Direction.NORTH -> {
				consumer.addVertex(pose,minX,minY,minZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,maxX,minY,minZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,maxX,maxY,minZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,minX,maxY,minZ).setColor(color).setLineWidth(lineWidth)
			}
			Direction.SOUTH -> {
				consumer.addVertex(pose,minX,minY,maxZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,maxX,minY,maxZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,maxX,maxY,maxZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,minX,maxY,maxZ).setColor(color).setLineWidth(lineWidth)
			}
			Direction.EAST -> {
				consumer.addVertex(pose,maxX,minY,maxZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,maxX,maxY,maxZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,maxX,maxY,minZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,maxX,minY,minZ).setColor(color).setLineWidth(lineWidth)
			}
			Direction.WEST -> {
				consumer.addVertex(pose,minX,minY,maxZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,minX,maxY,maxZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,minX,maxY,minZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,minX,minY,minZ).setColor(color).setLineWidth(lineWidth)
			}
			Direction.UP -> {
				consumer.addVertex(pose,minX,maxY,minZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,maxX,maxY,minZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,maxX,maxY,maxZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,minX,maxY,maxZ).setColor(color).setLineWidth(lineWidth)
			}
			Direction.DOWN -> {
				consumer.addVertex(pose,minX,minY,minZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,maxX,minY,minZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,maxX,minY,maxZ).setColor(color).setLineWidth(lineWidth)
				consumer.addVertex(pose,minX,minY,maxZ).setColor(color).setLineWidth(lineWidth)
			}
		}
	}

	fun drawLine(
		firstPos: Vector3f,
		secondPos: Vector3f,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		draw(WpcModRenderPipelines.LINES) { consumer ->
			val pose: PoseStack.Pose = matrixStack.last()
			val normal = (secondPos - firstPos).normalize()
			val color = color.getEffectiveColourRGB()

			consumer.addVertex(pose, secondPos)
				.setColor(color)
				.setNormal(pose, normal)
				.setLineWidth(lineWidth)

			consumer.addVertex(pose, firstPos)
				.setColor(color)
				.setNormal(pose, normal)
				.setLineWidth(lineWidth)
		}
	}

	private fun draw(pipeline: RenderPipeline, render: (buffer: VertexConsumer) -> Unit) {
		matrixStack.pushPose()
		matrixStack.translate(-camera.pos)

		render(WpcModRenderer.getBuffer(pipeline))

		matrixStack.popPose()
	}
}