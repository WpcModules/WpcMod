package net.wapic.wpcmod.util.render

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import java.awt.Color


object RenderUtils {
    private val mc: MinecraftClient = MinecraftClient.getInstance()

    fun drawBoundingBox(worldRenderContext: WorldRenderContext, boundingBox: Box, lineWidth: Double = 2.0, color: Color = Color(255, 255, 255)){
        drawBox(worldRenderContext, boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ, lineWidth, color)
    }

    fun drawBox(worldRenderContext: WorldRenderContext, minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double, lineWidth: Double = 2.0, color: Color = Color(255, 255, 255)){
        if(worldRenderContext.frustum()?.isVisible(Box(minX, minY, minZ, maxX, maxY, maxZ)) == false) return

        val matrixStack = worldRenderContext.matrixStack() ?: return
        val camera = worldRenderContext.camera().pos

        matrixStack.push()
        matrixStack.multiplyPositionMatrix(worldRenderContext.positionMatrix())
        matrixStack.translate(-camera.x, -camera.y, -camera.z)

        val vertexConsumerProvider: VertexConsumerProvider.Immediate = worldRenderContext.consumers() as VertexConsumerProvider.Immediate
        val layer: RenderLayer = RenderLayers.getLines(lineWidth)
        val bufferBuilder: VertexConsumer = vertexConsumerProvider.getBuffer(layer)

        VertexRendering.drawBox(matrixStack, bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, color.red * 255f, color.green * 255f, color.blue * 255f, color.alpha * 255f)
        vertexConsumerProvider.draw(layer)

        matrixStack.pop()
    }

    fun drawTracer(worldRenderContext: WorldRenderContext, x: Double, y: Double, z: Double, lineWidth: Double = 2.0, color: Color = Color(255, 255, 255)){
        val viewBobbing = mc.options.bobView.value
        mc.options.bobView.value = false

        val camera = worldRenderContext.camera()
        val cameraPoint: Vec3d = camera.pos.add(Vec3d.fromPolar(camera.pitch, camera.yaw))
        drawLine(worldRenderContext, cameraPoint.x, cameraPoint.y, cameraPoint.z, x, y, z, lineWidth, color)

        mc.options.bobView.value = viewBobbing
    }

    fun drawLine(worldRenderContext: WorldRenderContext, x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double, lineWidth: Double = 2.0, color: Color = Color(255, 255, 255)){
        val matrixStack = worldRenderContext.matrixStack() ?: return
        val camera: Vec3d = worldRenderContext.camera().pos

        matrixStack.push()
        matrixStack.multiplyPositionMatrix(worldRenderContext.positionMatrix())
        matrixStack.translate(-camera.x, -camera.y, -camera.z)

        val entry: MatrixStack.Entry = matrixStack.peek()

        val vertexConsumerProvider: VertexConsumerProvider.Immediate = worldRenderContext.consumers() as VertexConsumerProvider.Immediate
        val layer: RenderLayer = RenderLayers.getLines(lineWidth)
        val bufferBuilder: VertexConsumer = vertexConsumerProvider.getBuffer(layer)

        val normal = Vec3d(x2, y2, z2).toVector3f().sub(x1.toFloat(), y1.toFloat(), z1.toFloat()).normalize()

        bufferBuilder
            .vertex(entry, x1.toFloat(), y1.toFloat(), z1.toFloat())
            .color(color.red, color.green, color.blue, color.alpha)
            .normal(entry, normal)

        bufferBuilder
            .vertex(entry, x2.toFloat(), y2.toFloat(), z2.toFloat())
            .color(color.red, color.green, color.blue, color.alpha)
            .normal(entry, normal)

        vertexConsumerProvider.draw(layer)
        matrixStack.pop()
    }
}