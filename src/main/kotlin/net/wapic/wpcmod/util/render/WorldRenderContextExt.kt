package net.wapic.wpcmod.util.render

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.*
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.text.OrderedText
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.VecUtils.unaryMinus
import java.awt.Color

fun WorldRenderContext.drawText(text: OrderedText, pos: Vec3d, scale: Float, depth: Boolean) {
	val matrixStack = this.matrixStack() ?: return

	matrixStack.push()
	val scale = scale * 0.025f
	val matrix = matrixStack.peek().positionMatrix

	matrixStack.multiplyPositionMatrix(positionMatrix())
	matrixStack.translate(pos)
	matrixStack.translate(-camera().pos)
	matrixStack.multiply(camera().rotation)
	matrixStack.scale(scale, -scale, scale)

	val consumers = this.consumers() as VertexConsumerProvider.Immediate

	MC.textRenderer.draw(
		text, -MC.textRenderer.getWidth(text) / 2f, 0f, -1, true, matrix, consumers,
		if (depth) TextRenderer.TextLayerType.NORMAL else TextRenderer.TextLayerType.SEE_THROUGH,
		0, LightmapTextureManager.MAX_LIGHT_COORDINATE
	)

	consumers.draw()
	matrixStack.pop()
}

fun WorldRenderContext.drawBeaconBeam(position: BlockPos, color: Color) {
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
}

fun WorldRenderContext.drawBoundingBox(
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

fun WorldRenderContext.drawBox(
	minX: Double,
	minY: Double,
	minZ: Double,
	maxX: Double,
	maxY: Double,
	maxZ: Double, color: Color = Color(255, 255, 255), lineWidth: Double = 2.0
) {
	val matrixStack = matrixStack() ?: return
	val camera = camera().pos

	matrixStack.push()
	matrixStack.multiplyPositionMatrix(positionMatrix())
	matrixStack.translate(-camera.x, -camera.y, -camera.z)

	val vertexConsumerProvider: VertexConsumerProvider.Immediate = consumers() as VertexConsumerProvider.Immediate
	val layer: RenderLayer = RenderLayers.getLines(lineWidth)
	val bufferBuilder: VertexConsumer = vertexConsumerProvider.getBuffer(layer)

	VertexRendering.drawBox(
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

	vertexConsumerProvider.draw(layer)

	matrixStack.pop()
}

fun WorldRenderContext.drawTracer(
	entity: Entity,
	color: Color = Color(255, 255, 255),
	lineWidth: Double = 2.0
) {
	val tickProgress = tickCounter().dynamicDeltaTicks

	val x = MathHelper.lerp(tickProgress.toDouble(), entity.lastRenderX, entity.x)
	val y = MathHelper.lerp(tickProgress.toDouble(), entity.lastRenderY, entity.y)
	val z = MathHelper.lerp(tickProgress.toDouble(), entity.lastRenderZ, entity.z)

	drawTracer(x, y, z, color, lineWidth)
}

fun WorldRenderContext.drawTracer(
	pos: Vec3d,
	color: Color = Color(255, 255, 255),
	lineWidth: Double = 2.0
) {
	drawTracer(pos.x, pos.y, pos.z, color, lineWidth)
}

fun WorldRenderContext.drawTracer(
	x: Double,
	y: Double,
	z: Double, color: Color = Color(255, 255, 255), lineWidth: Double = 2.0
) {
	val viewBobbing = MC.options.bobView.value
	MC.options.bobView.value = false

	val camera = camera()
	val cameraPoint: Vec3d = camera.pos.add(Vec3d.fromPolar(camera.pitch, camera.yaw))
	drawLine(cameraPoint.x, cameraPoint.y, cameraPoint.z, x, y, z, color, lineWidth)

	MC.options.bobView.value = viewBobbing
}

fun WorldRenderContext.drawLine(
	x1: Double,
	y1: Double,
	z1: Double,
	x2: Double,
	y2: Double,
	z2: Double,
	color: Color = Color(255, 255, 255),
	lineWidth: Double = 2.0
) {
	val matrixStack = matrixStack() ?: return
	val camera: Vec3d = camera().pos

	matrixStack.push()
	matrixStack.multiplyPositionMatrix(positionMatrix())
	matrixStack.translate(-camera.x, -camera.y, -camera.z)

	val entry: MatrixStack.Entry = matrixStack.peek()

	val vertexConsumerProvider: VertexConsumerProvider.Immediate = consumers() as VertexConsumerProvider.Immediate
	val layer: RenderLayer = RenderLayers.getLines(lineWidth)
	val bufferBuilder: VertexConsumer = vertexConsumerProvider.getBuffer(layer)

	val normal = Vec3d(x2, y2, z2).toVector3f().sub(x1.toFloat(), y1.toFloat(), z1.toFloat()).normalize()

	bufferBuilder.vertex(entry, x1.toFloat(), y1.toFloat(), z1.toFloat())
		.color(color.red, color.green, color.blue, color.alpha).normal(entry, normal)

	bufferBuilder.vertex(entry, x2.toFloat(), y2.toFloat(), z2.toFloat())
		.color(color.red, color.green, color.blue, color.alpha).normal(entry, normal)

	vertexConsumerProvider.draw(layer)
	matrixStack.pop()
}