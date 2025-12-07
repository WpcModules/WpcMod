package net.wapic.wpcmod.util.render

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.state.CameraRenderState
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.DeltaTracker
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShapeRenderer
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.util.profiling.ProfilerFiller
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.VecUtils.unaryMinus

class WorldRenderContext {
	val matrixStack: PoseStack
	val world: ClientLevel
	val consumer: MultiBufferSource.BufferSource
	val tickCounter: DeltaTracker
	val camera: CameraRenderState
	val profiler: ProfilerFiller

	constructor(
		matrixStack: PoseStack,
		world: ClientLevel,
		consumer: MultiBufferSource.BufferSource,
		tickCounter: DeltaTracker,
		camera: CameraRenderState,
		profiler: ProfilerFiller
	) {
		this.matrixStack = matrixStack
		this.world = world
		this.consumer = consumer
		this.tickCounter = tickCounter
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

		MC.textRenderer.drawInBatch(
			text, -MC.textRenderer.width(text) / 2f, 0f, -1, true, matrix, consumer,
			if (depth) Font.DisplayMode.NORMAL else Font.DisplayMode.SEE_THROUGH,
			0, LightTexture.FULL_BRIGHT
		)

		consumer.endBatch()

		matrixStack.popPose()
	}

/*	fun WorldRenderContext.drawBeaconBeam(position: BlockPos, color: Color) {
		val matrix = matrixStack() ?: return
		val bufferSource = consumers() as? VertexConsumerProvider.Immediate ?: return
		val camera = camera().pos

		matrix.push()
		matrix.multiplyPositionMatrix(positionMatrix())
		matrix.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z)

		val length = camera.subtract(position.toCenterPos()).horizontalLength().toFloat()
		val scale = if (MC.player != null && MC.player?.isUsingSpyglass == true) 1.0f else maxOf(1.0f, length / 96.0f)

		BeaconBlockEntityRenderer.renderBeam(
			matrix, bufferSource, BeaconBlockEntityRenderer.BEAM_TEXTURE,
			tickCounter().getTickProgress(true), scale, world().time, 0, 319, color.rgb, 0.2f * scale, 0.25f * scale
		)

		matrix.pop()
	}*/

	fun drawBoundingBox(
		pos: Vec3,
		width: Float, height: Float,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Double = 2.0
	) {
		val width = width / 2.0
		val height = height / 2.0
		drawBox(
			pos.x - width,
			pos.y - height,
			pos.z - width,
			pos.x + width,
			pos.y + height,
			pos.z + width,
			color,
			lineWidth,
		)
	}

	fun drawBoundingBox(
		boundingBox: AABB,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Double = 2.0
	) {
		drawBox(
			boundingBox.minX,
			boundingBox.minY,
			boundingBox.minZ,
			boundingBox.maxX,
			boundingBox.maxY,
			boundingBox.maxZ,
			color,
			lineWidth,
		)
	}

	fun drawFilledBoxWithOutline(
		pos: Vec3,
		width: Double, height: Double,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		outlineColor: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Double = 2.0
	) {
		val width = width / 2
		val height = height / 2
		with(pos) {
			drawFilledBox(x - width, y - height, z - width, x + width, y + height, z + width, color)
			drawBox(x - width, y - height, z - width, x + width, y + height, z + width, outlineColor, lineWidth)
		}
	}

	fun drawFilledBoxWithOutline(
		minX: Double,
		minY: Double,
		minZ: Double,
		maxX: Double,
		maxY: Double,
		maxZ: Double,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		outlineColor: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Double = 2.0
	) {
		drawFilledBox(minX, minY, minZ, maxX, maxY, maxZ, color)
		drawBox(minX, minY, minZ, maxX, maxY, maxZ, outlineColor, lineWidth)
	}

	fun drawFilledBoxWithOutline(
		boundingBox: AABB,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		outlineColor: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Double = 2.0
	) {
		drawFilledBoxWithOutline(
			boundingBox.minX, boundingBox.minY, boundingBox.minZ,
			boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ,
			color, outlineColor,
			lineWidth
		)
	}

	fun drawFilledBoundingBox(
		boundingBox: AABB,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255)
	) {
		drawFilledBox(
			boundingBox.minX, boundingBox.minY, boundingBox.minZ,
			boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ,
			color,
		)
	}

	fun drawFilledBox(
		minX: Double, minY: Double, minZ: Double,
		maxX: Double, maxY: Double, maxZ: Double,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255)
	) {

		matrixStack.pushPose()
		matrixStack.mulPose(matrixStack.last().pose())
		matrixStack.translate(-camera.pos)

		val layer: RenderType = RenderLayers.FILLED_BOX
		val bufferBuilder = consumer.getBuffer(layer)
		val color = color.getEffectiveColour()

		ShapeRenderer.addChainedFilledBoxVertices(
			matrixStack,
			bufferBuilder,
			minX, minY, minZ,
			maxX, maxY, maxZ,
			color.red * 255f,
			color.green * 255f,
			color.blue * 255f,
			color.alpha * 255f
		)

		consumer.endBatch()

		matrixStack.popPose()
	}

	fun drawBox(
		minX: Double, minY: Double, minZ: Double,
		maxX: Double, maxY: Double, maxZ: Double,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Double = 2.0
	) {
		matrixStack.pushPose()
		matrixStack.translate(-camera.pos)

		val layer: RenderType = RenderLayers.getLines(lineWidth)
		WpcModRenderPipelines.LINES
		val bufferBuilder: VertexConsumer = consumer.getBuffer(layer)
		val color = color.getEffectiveColour()

		ShapeRenderer.renderLineBox(
			matrixStack.last(),
			bufferBuilder,
			minX,
			minY,
			minZ,
			maxX,
			maxY,
			maxZ,
			color.red * 255f,
			color.green * 255f,
			color.blue * 255f,
			color.alpha * 255f
		)

		matrixStack.popPose()
	}

	fun drawTracer(
		pos: Vec3,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Double = 2.0
	) {
		drawTracer(pos.x, pos.y, pos.z, color, lineWidth)
	}

	fun drawTracer(
		x: Double, y: Double, z: Double,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Double = 2.0
	) {
		val viewBobbing = MC.options.bobView().get()
		MC.options.bobView().set(false)

		val cameraPoint: Vec3 = camera.pos.add(Vec3.directionFromRotation(MC.player?.xRot ?: 0f, MC.player?.yRot ?: 0f))
		drawLine(cameraPoint.x, cameraPoint.y, cameraPoint.z, x, y, z, color, lineWidth)

		MC.options.bobView().set(viewBobbing)
	}

	fun drawLine(
		x1: Double, y1: Double, z1: Double,
		x2: Double, y2: Double, z2: Double,
		color: ChromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255),
		lineWidth: Double = 2.0
	) {

		matrixStack.pushPose()
		matrixStack.mulPose(matrixStack.last().pose())
		matrixStack.translate(-camera.pos)

		val entry: PoseStack.Pose = matrixStack.last()

		val layer: RenderType = RenderLayers.getLines(lineWidth)
		val bufferBuilder: VertexConsumer = consumer.getBuffer(layer)

		val normal = Vec3(x2, y2, z2).toVector3f().sub(x1.toFloat(), y1.toFloat(), z1.toFloat()).normalize()
		val color = color.getEffectiveColour()

		bufferBuilder.addVertex(entry, x1.toFloat(), y1.toFloat(), z1.toFloat())
			.setColor(color.red, color.green, color.blue, color.alpha).setNormal(entry, normal)

		bufferBuilder.addVertex(entry, x2.toFloat(), y2.toFloat(), z2.toFloat())
			.setColor(color.red, color.green, color.blue, color.alpha).setNormal(entry, normal)

		consumer.endBatch(layer)
		matrixStack.popPose()
	}
}