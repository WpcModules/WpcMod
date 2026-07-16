package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.gizmos.GizmoStyle
import net.minecraft.gizmos.Gizmos
import net.minecraft.util.ARGB
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.VecUtils.unaryMinus
import org.joml.Vector3f
import org.joml.minus

class WorldRenderContext {
	val matrixStack: PoseStack
	val camera: CameraRenderState
	val profiler: ProfilerFiller

	constructor(
		matrixStack: PoseStack,
		camera: CameraRenderState,
		profiler: ProfilerFiller
	) {
		this.matrixStack = matrixStack
		this.camera = camera
		this.profiler = profiler
	}

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
		pos: Vec3, width: Float, height: Float,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		outlineColor: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		with(pos) {
			drawFilledBox(x - width, y - height, z - width, x + width, y + height, z + width, color)
			drawBox(x - width, y - height, z - width, x + width, y + height, z + width, outlineColor, lineWidth)
		}
	}

	fun drawFilledBoxWithOutline(
		minX: Double, minY: Double, minZ: Double,
		maxX: Double, maxY: Double, maxZ: Double,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		outlineColor: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		drawFilledBox(minX, minY, minZ, maxX, maxY, maxZ, color)
		drawBox(minX, minY, minZ, maxX, maxY, maxZ, outlineColor, lineWidth)
	}

	fun drawFilledBoxWithOutline(
		aabb: AABB,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		outlineColor: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		drawFilledBoxWithOutline(
			aabb.minX, aabb.minY, aabb.minZ,
			aabb.maxX, aabb.maxY, aabb.maxZ,
			color, outlineColor,
			lineWidth
		)
	}

	fun drawFilledBoundingBox(
		aabb: AABB,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
	) {
		drawFilledBox(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, color)
	}

	fun drawFilledBoundingBox(
		pos: Vec3, width: Float, height: Float,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
	) {
		val width = width / 2
		val height = height / 2
		drawFilledBox(
			pos.x - width, pos.y - height, pos.z - width,
			pos.x + width, pos.y + height, pos.z + width,
			color,
		)
	}

	fun drawBoundingBox(
		pos: Vec3, width: Float, height: Float,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		val width = width / 2.0
		val height = height / 2.0
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
		draw(WpcModRenderTypes.LINES) { consumer ->
			val color = color.getEffectiveColour()

			Gizmos.cuboid(aabb, GizmoStyle.stroke(ARGB.color(color.alpha, color.rgb), lineWidth)).setAlwaysOnTop()
		}
	}

	fun drawTracer(
		pos: Vec3,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		drawTracer(pos.x, pos.y, pos.z, color, lineWidth)
	}

	fun drawTracer(
		x: Double, y: Double, z: Double,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		val viewBobbing = MC.options.bobView().get()
		MC.options.bobView().set(false)

		val forward = camera.orientation.transform(Vector3f(0f, 0f, -1f))

		val cameraPos = camera.pos.toVector3f().add(forward)
		val targetPos = Vec3(x, y, z).toVector3f()

		drawLine(cameraPos, targetPos, color, lineWidth)

		MC.options.bobView().set(viewBobbing)
	}

	fun drawFilledBox(
		minX: Double, minY: Double, minZ: Double,
		maxX: Double, maxY: Double, maxZ: Double,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
	) {
		draw(WpcModRenderTypes.FILLED_BOX) { consumer ->
			val color = color.getEffectiveColour()
			val aabb = AABB(minX, minY, minZ, maxX, maxY, maxZ)
			Gizmos.cuboid(aabb, GizmoStyle.fill(ARGB.color(color.alpha, color.rgb))).setAlwaysOnTop()
		}
	}

	fun drawBox(
		minX: Double, minY: Double, minZ: Double,
		maxX: Double, maxY: Double, maxZ: Double,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		val shape = AABB(minX, minY, minZ, maxX, maxY, maxZ)
		drawBoundingBox(shape, color, lineWidth)
	}

	fun drawLine(
		firstPos: Vector3f,
		secondPos: Vector3f,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Float = 2f
	) {
		draw(WpcModRenderTypes.LINES) { consumer ->
			val pose: PoseStack.Pose = matrixStack.last()
			val normal = (secondPos - firstPos).normalize()
			val color = color.getEffectiveColour()

			consumer.addVertex(pose, secondPos)
				.setColor(color.red, color.green, color.blue, color.alpha)
				.setNormal(pose, normal)
				.setLineWidth(lineWidth)

			consumer.addVertex(pose, firstPos)
				.setColor(color.red, color.green, color.blue, color.alpha)
				.setNormal(pose, normal)
				.setLineWidth(lineWidth)
		}
	}

	private fun draw(renderType: RenderType, render: (consumer: VertexConsumer) -> Unit) {
		matrixStack.pushPose()
		matrixStack.translate(-camera.pos)

		//val consumer = bufferSource.getBuffer(renderType)

		//render(consumer)

		//bufferSource.endBatch(renderType)
		matrixStack.popPose()
	}
}