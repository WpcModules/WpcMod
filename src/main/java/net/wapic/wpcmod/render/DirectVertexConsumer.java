package net.wapic.wpcmod.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.wapic.wpcmod.mixin.accessors.BufferBuilderAccessor;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A direct view into a BufferBuilder's internal memory state.
 * Allows for direct writing of sequential data, bypassing the offsets BufferBuilder uses.
 * This is useful if you have a VertexFormat with duplicate or esoteric elements, that BufferBuilder doesn't know about.
 * <h2>A word of caution</h2>
 * Vanilla BufferBuilder has guards against wrong ordering, underflowing, overflowing etc. This class does not care.
 * <p>This class grants immense power. With that power comes many abilities to shoot yourself in the foot.
 * Here be common footguns, and what happens when they're fired:
 * <ul>
 *     <li>Writing too much data: buffer overflow exception is thrown from the ByteBuffer</li>
 *     <li>Writing too little data: New vertices' data will mistakenly end up in the previous vertex. May cause an overflow or incorrect interpretation of the data</li>
 *     <li>Writing the wrong kind of data: The slots the data go into will be interpreted incorrectly, or overflow</li>
 * </ul>
 */
public class DirectVertexConsumer implements VertexConsumer {
	private final BufferBuilder original;
	private final VertexFormat format;
	private ByteBuffer into;

	/**
	 * Creates a new DirectVertexConsumer
	 *
	 * @param original       Original, underlying bufferbuffer to write data into
	 * @param skipFirstAlloc Don't make a new vertex, overwrite the current one
	 */
	public DirectVertexConsumer(BufferBuilder original, boolean skipFirstAlloc) {
		this.original = original;
		BufferBuilderAccessor bfa = ((BufferBuilderAccessor) original);
		format = bfa.getFormat();
		long ptr;
		if (!skipFirstAlloc) {
			ptr = bfa.beginNewVertex();
		} else ptr = bfa.getMeTheFuckingPointerOfThisBitch();
		into = MemoryUtil.memByteBuffer(ptr, format.getVertexSize());
		into.order(ByteOrder.nativeOrder());
	}

	private void checkEnd() {
		if (!into.hasRemaining()) {
			newVert();
		}
	}

	private void newVert() {
		BufferBuilderAccessor bfa = ((BufferBuilderAccessor) original);
		into = MemoryUtil.memByteBuffer(bfa.beginNewVertex(), format.getVertexSize());
		into.order(ByteOrder.nativeOrder());
	}

	/**
	 * Returns the ByteBuffer pointing to the current vertex' data. A new ByteBuffer must be obtained for <b>every vertex</b>. This method returns a new one when the previous one is filled.
	 * Writing multiple vertices into one ByteBuffer will overflow the buffer.
	 *
	 * @return ByteBuffer pointing to the current vertex' data
	 */
	public ByteBuffer getCurrentVertexData() {
		checkEnd();
		return into;
	}

	@Override
	public VertexConsumer addVertex(float x, float y, float z) {
		checkEnd();
		into.putFloat(x);
		into.putFloat(y);
		into.putFloat(z);
		return this;
	}

	@Override
	public void addVertex(float x, float y, float z, int color, float u, float v, int packedOverlay, int packedLight, float normalX, float normalY, float normalZ) {
		addVertex(x, y, z);
		setColor(color);
		setUv(u, v);
		setOverlay(packedOverlay);
		setLight(packedLight);
		setNormal(normalX, normalY, normalZ);
	}

	@Override
	public VertexConsumer setColor(float red, float green, float blue, float alpha) {
		return setColor((int) (red * 255.0F), (int) (green * 255.0F), (int) (blue * 255.0F), (int) (alpha * 255.0F));
	}

	@Override
	public VertexConsumer setColor(int red, int green, int blue, int alpha) {
		return setColor(ARGB.color(alpha, red, green, blue));
	}

	@Override
	public VertexConsumer setColor(int argb) {
		checkEnd();
		int i = ARGB.toABGR(argb);
		into.putInt(i);
		return this;
	}

	@Override
	public VertexConsumer setUv(float u, float v) {
		checkEnd();
		into.putFloat(u);
		into.putFloat(v);
		return this;
	}

	@Override
	public VertexConsumer setUv1(int u, int v) {
		checkEnd();
		into.putShort((short) u);
		into.putShort((short) v);
		return this;
	}

	@Override
	public VertexConsumer setOverlay(int uv) {
		checkEnd();
		into.putInt(uv);
		return this;
	}

	@Override
	public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float red, float green, float blue, float alpha, int packedLight, int packedOverlay) {
		VertexConsumer.super.putBulkData(pose, quad, red, green, blue, alpha, packedLight, packedOverlay);
	}

	@Override
	public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float[] brightness, float red, float green, float blue, float alpha, int[] lightmap, int packedOverlay) {
		VertexConsumer.super.putBulkData(pose, quad, brightness, red, green, blue, alpha, lightmap, packedOverlay);
	}

	@Override
	public VertexConsumer addVertex(Vector3fc pos) {
		return VertexConsumer.super.addVertex(pos);
	}

	@Override
	public VertexConsumer addVertex(PoseStack.Pose pose, Vector3f pos) {
		return VertexConsumer.super.addVertex(pose, pos);
	}

	@Override
	public VertexConsumer addVertex(PoseStack.Pose pose, float x, float y, float z) {
		return VertexConsumer.super.addVertex(pose, x, y, z);
	}

	@Override
	public VertexConsumer addVertex(Matrix4fc pose, float x, float y, float z) {
		return VertexConsumer.super.addVertex(pose, x, y, z);
	}

	@Override
	public VertexConsumer addVertexWith2DPose(Matrix3x2fc pose, float x, float y) {
		return VertexConsumer.super.addVertexWith2DPose(pose, x, y);
	}

	@Override
	public VertexConsumer setNormal(PoseStack.Pose pose, float normalX, float normalY, float normalZ) {
		return VertexConsumer.super.setNormal(pose, normalX, normalY, normalZ);
	}

	@Override
	public VertexConsumer setNormal(PoseStack.Pose pose, Vector3f normalVector) {
		return VertexConsumer.super.setNormal(pose, normalVector);
	}

	@Override
	public VertexConsumer setUv2(int u, int v) {
		return setUv1(u, v);
	}

	@Override
	public VertexConsumer setLight(int uv) {
		return setOverlay(uv);
	}

	private static byte floatToByte(float f) {
		return (byte) ((int) (Mth.clamp(f, -1.0F, 1.0F) * 127.0F) & 0xFF);
	}

	@Override
	public VertexConsumer setNormal(float x, float y, float z) {
		checkEnd();
		into.put(floatToByte(x));
		into.put(floatToByte(y));
		into.put(floatToByte(z));
		return this;
	}

	@Override
	public VertexConsumer setLineWidth(float lineWidth) {
		return this;
	}
}
