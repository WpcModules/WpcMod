package net.wapic.wpcmod.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import net.wapic.wpcmod.events.WorldRenderEvent;
import net.wapic.wpcmod.util.render.WorldRenderContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

	@Shadow
	protected abstract void checkPoseStack(PoseStack poseStack);

	@Shadow
	@Final
	private LevelTargetBundle targets;

	@Inject(method = "addMainPass", at = @At("TAIL"))
	private void onRenderWorld(FrameGraphBuilder frame, FeatureRenderDispatcher.PreparedFrame featureFrame, GpuBufferSlice terrainFog, LevelRenderState levelRenderState, ProfilerFiller profiler, ChunkSectionsToRender chunkSectionsToRender, CallbackInfo ci) {
		FramePass framePass = frame.addPass("wpcmod:render_world");

		this.targets.main = framePass.readsAndWrites(this.targets.main);

		framePass.executes(() -> {
			PoseStack matrixStack = new PoseStack();
			//MultiBufferSource.BufferSource immediate = this.renderBuffers.bufferSource();
			WorldRenderContext worldRenderContext = new WorldRenderContext(matrixStack, levelRenderState.cameraRenderState, profiler);
			WorldRenderEvent.EVENT.invoker().onRenderWorld(worldRenderContext);
			//immediate.endBatch();
			this.checkPoseStack(matrixStack);
		});
	}
}
