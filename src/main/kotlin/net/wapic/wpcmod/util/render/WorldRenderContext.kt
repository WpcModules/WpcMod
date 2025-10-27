package net.wapic.wpcmod.util.render

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexRendering
import net.minecraft.client.render.state.CameraRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.world.ClientWorld
import net.minecraft.text.OrderedText
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.profiler.Profiler
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.VecUtils.unaryMinus
import org.joml.Matrix4f
import java.awt.Color

class WorldRenderContext {
	private val matrixStack: MatrixStack
	private val world: ClientWorld
	private val consumer: VertexConsumerProvider.Immediate
	private val tickCounter: RenderTickCounter
	private val camera: CameraRenderState
	private val profiler: Profiler

	constructor(
		matrixStack: MatrixStack,
		world: ClientWorld,
		consumer: VertexConsumerProvider.Immediate,
		tickCounter: RenderTickCounter,
		camera: CameraRenderState,
		profiler: Profiler
	) {
		this.matrixStack = matrixStack
		this.world = world
		this.consumer = consumer
		this.tickCounter = tickCounter
		this.camera = camera
		this.profiler = profiler
	}

	fun matrixStack(): MatrixStack {
		return matrixStack
	}

	fun world(): ClientWorld {
		return world
	}

	fun positionMatrix(): Matrix4f {
		return matrixStack.peek().positionMatrix
	}

	fun tickCounter(): RenderTickCounter {
		return tickCounter
	}

	fun camera(): CameraRenderState {
		return camera
	}

	fun drawText(text: OrderedText, pos: Vec3d, scale: Float, depth: Boolean) {
		val matrixStack = this.matrixStack()

		matrixStack.push()
		val scale = scale * 0.025f
		val matrix = matrixStack.peek().positionMatrix

		matrixStack.multiplyPositionMatrix(positionMatrix())
		matrixStack.translate(pos)
		matrixStack.translate(-camera.pos)
		matrixStack.multiply(camera().orientation)
		matrixStack.scale(scale, -scale, scale)

		MC.textRenderer.draw(
			text, -MC.textRenderer.getWidth(text) / 2f, 0f, -1, true, matrix, consumer,
			if (depth) TextRenderer.TextLayerType.NORMAL else TextRenderer.TextLayerType.SEE_THROUGH,
			0, LightmapTextureManager.MAX_LIGHT_COORDINATE
		)

		consumer.draw()

		matrixStack.pop()
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
		pos: Vec3d,
		width: Float,
		height: Float,
		color: Color = Color(255, 255, 255),
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
		boundingBox: Box,
		color: Color = Color(255, 255, 255),
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
		pos: Vec3d,
		width: Double,
		height: Double,
		color: Color = Color(255, 255, 255),
		outlineColor: Color = Color(255, 255, 255),
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
		color: Color = Color(255, 255, 255),
		outlineColor: Color = Color(255, 255, 255),
		lineWidth: Double = 2.0
	) {
		drawFilledBox(minX, minY, minZ, maxX, maxY, maxZ, color)
		drawBox(minX, minY, minZ, maxX, maxY, maxZ, outlineColor, lineWidth)
	}

	fun drawFilledBoxWithOutline(
		boundingBox: Box,
		color: Color = Color(255, 255, 255),
		outlineColor: Color = Color(255, 255, 255),
		lineWidth: Double = 2.0
	) {
		drawFilledBoxWithOutline(
			boundingBox.minX,
			boundingBox.minY,
			boundingBox.minZ,
			boundingBox.maxX,
			boundingBox.maxY,
			boundingBox.maxZ,
			color,
			outlineColor,
			lineWidth
		)
	}

	fun drawFilledBoundingBox(
		boundingBox: Box,
		color: Color = Color(255, 255, 255),
	) {
		drawFilledBox(
			boundingBox.minX,
			boundingBox.minY,
			boundingBox.minZ,
			boundingBox.maxX,
			boundingBox.maxY,
			boundingBox.maxZ,
			color,
		)
	}

	fun drawFilledBox(
		minX: Double,
		minY: Double,
		minZ: Double,
		maxX: Double,
		maxY: Double,
		maxZ: Double, color: Color = Color(255, 255, 255)
	) {

		matrixStack.push()
		matrixStack.multiplyPositionMatrix(positionMatrix())
		matrixStack.translate(-camera.pos)

		val layer: RenderLayer = RenderLayers.FILLED_BOX
		val bufferBuilder = consumer.getBuffer(layer)

		VertexRendering.drawFilledBox(
			matrixStack,
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

		consumer.draw()

		matrixStack.pop()
	}

	fun drawBox(
		minX: Double,
		minY: Double,
		minZ: Double,
		maxX: Double,
		maxY: Double,
		maxZ: Double, color: Color = Color(255, 255, 255), lineWidth: Double = 2.0
	) {
		matrixStack.push()
		matrixStack.translate(-camera.pos)
		//matrixStack.multiplyPositionMatrix(positionMatrix())

		val layer: RenderLayer = RenderLayers.getLines(lineWidth)
		WpcModRenderPipelines.LINES
		val bufferBuilder: VertexConsumer = consumer.getBuffer(layer)
		val bufferBuilder: BufferBuilder = BufferBuilder()

		VertexRendering.drawBox(
			matrixStack.peek(),
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

		matrixStack.pop()
	}

	fun drawTracer(
		pos: Vec3d,
		color: Color = Color(255, 255, 255),
		lineWidth: Double = 2.0
	) {
		drawTracer(pos.x, pos.y, pos.z, color, lineWidth)
	}

	fun drawTracer(
		x: Double,
		y: Double,
		z: Double, color: Color = Color(255, 255, 255), lineWidth: Double = 2.0
	) {
		val viewBobbing = MC.options.bobView.value
		MC.options.bobView.value = false

		val cameraPoint: Vec3d = camera.pos.add(Vec3d.fromPolar(camera.orientation.x, camera.orientation.y))
		drawLine(cameraPoint.x, cameraPoint.y, cameraPoint.z, x, y, z, color, lineWidth)

		MC.options.bobView.value = viewBobbing
	}

	fun drawLine(
		x1: Double,
		y1: Double,
		z1: Double,
		x2: Double,
		y2: Double,
		z2: Double,
		color: Color = Color(255, 255, 255),
		lineWidth: Double = 2.0
	) {

		matrixStack.push()
		matrixStack.multiplyPositionMatrix(positionMatrix())
		matrixStack.translate(-camera.pos)

		val entry: MatrixStack.Entry = matrixStack.peek()

		val layer: RenderLayer = RenderLayers.getLines(lineWidth)
		val bufferBuilder: VertexConsumer = consumer.getBuffer(layer)

		val normal = Vec3d(x2, y2, z2).toVector3f().sub(x1.toFloat(), y1.toFloat(), z1.toFloat()).normalize()

		bufferBuilder.vertex(entry, x1.toFloat(), y1.toFloat(), z1.toFloat())
			.color(color.red, color.green, color.blue, color.alpha).normal(entry, normal)

		bufferBuilder.vertex(entry, x2.toFloat(), y2.toFloat(), z2.toFloat())
			.color(color.red, color.green, color.blue, color.alpha).normal(entry, normal)

		consumer.draw(layer)
		matrixStack.pop()
	}
}