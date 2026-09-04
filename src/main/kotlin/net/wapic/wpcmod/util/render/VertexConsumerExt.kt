package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import io.github.notenoughupdates.moulconfig.ChromaColour
import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.minus

fun VertexConsumer.addVertex(
	pose: PoseStack.Pose,
	x: Float, y: Float, z: Float,
	color: ChromaColour,
	lineWidth: Float
): VertexConsumer {
	return addVertex(pose, x, y, z).setColor(color.getEffectiveColourRGB()).setLineWidth(lineWidth)
}

fun VertexConsumer.addVertex(
	pose: PoseStack.Pose,
	pos: Vector3fc,
	color: ChromaColour,
	lineWidth: Float
): VertexConsumer {
	return addVertex(pose, pos).setColor(color.getEffectiveColourRGB()).setLineWidth(lineWidth)
}

fun VertexConsumer.addVertex(
	pose: PoseStack.Pose,
	x: Float, y: Float, z: Float,
	color: ChromaColour,
): VertexConsumer {
	return addVertex(pose, x, y, z).setColor(color.getEffectiveColourRGB())
}

fun VertexConsumer.line(
	pose: PoseStack.Pose,
	x1: Float, y1: Float, z1: Float,
	x2: Float, y2: Float, z2: Float,
	color: ChromaColour,
	lineWidth: Float
): VertexConsumer {
	val normal = (Vector3f(x1, y1, z1) - Vector3f(x2, y2, z2)).normalize()
	addVertex(pose, x1, y1, z1, color, lineWidth).setNormal(pose, normal)
	return addVertex(pose, x2, y2, z2, color, lineWidth).setNormal(pose, normal)
}
