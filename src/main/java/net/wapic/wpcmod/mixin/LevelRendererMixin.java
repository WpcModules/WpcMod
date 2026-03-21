package net.wapic.wpcmod.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import net.wapic.wpcmod.events.WorldRenderEvent;
import net.wapic.wpcmod.util.render.WorldRenderContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

	@Shadow
	@Nullable
	private ClientLevel level;

	@Shadow
	protected abstract void checkPoseStack(PoseStack matrices);

	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	@Final
	private LevelRenderState levelRenderState;

	@Shadow
	@Final
	private LevelTargetBundle targets;

	@Shadow
	@Final
	private RenderBuffers renderBuffers;

	@Inject(method = "addMainPass", at = @At("TAIL"))
	private void onRenderWorld(FrameGraphBuilder frameGraphBuilder, Frustum frustum, Matrix4f posMatrix, GpuBufferSlice fogBuffer, boolean renderBlockOutline, LevelRenderState state, DeltaTracker tickCounter, ProfilerFiller profiler, CallbackInfo ci) {
		if(level == null || minecraft.player == null) return;

		FramePass framePass = frameGraphBuilder.addPass("wpcmod:render_world");

		this.targets.main = framePass.readsAndWrites(this.targets.main);

		framePass.executes(() -> {
			PoseStack matrixStack = new PoseStack();
			MultiBufferSource.BufferSource immediate = this.renderBuffers.bufferSource();
			WorldRenderContext worldRenderContext = new WorldRenderContext(matrixStack, level, immediate, tickCounter, levelRenderState.cameraRenderState, profiler);
			WorldRenderEvent.EVENT.invoker().onRenderWorld(worldRenderContext);
			immediate.endBatch();
			this.checkPoseStack(matrixStack);
		});
	}
}
