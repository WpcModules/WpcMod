package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;
import net.wapic.wpcmod.events.WorldRenderEvent;
import net.wapic.wpcmod.features.entity.MobGlow;
import net.wapic.wpcmod.util.render.WorldRenderContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

	@Shadow
	@Nullable
	private ClientWorld world;

	@Shadow
	protected abstract void checkEmpty(MatrixStack matrices);

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	@Final
	private WorldRenderState worldRenderState;

	@Shadow
	@Final
	private DefaultFramebufferSet framebufferSet;

	@Shadow
	@Final
	private BufferBuilderStorage bufferBuilders;

	@Inject(method = "renderMain", at = @At("TAIL"))
	private void onRenderWorld(FrameGraphBuilder frameGraphBuilder, Frustum frustum, Matrix4f posMatrix, GpuBufferSlice fogBuffer, boolean renderBlockOutline, WorldRenderState state, RenderTickCounter tickCounter, Profiler profiler, CallbackInfo ci) {
		if(world == null || client.player == null) return;

		FramePass framePass = frameGraphBuilder.createPass("wpcmod:render_world");

		this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);

		Handle<Framebuffer> handle = this.framebufferSet.mainFramebuffer;
		framePass.setRenderer(() -> {
			RenderSystem.setShaderFog(fogBuffer);
			MatrixStack matrixStack = new MatrixStack();
			VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
			RenderSystem.outputColorTextureOverride = handle.get().getColorAttachmentView();
			RenderSystem.outputDepthTextureOverride = handle.get().getDepthAttachmentView();
			WorldRenderContext worldRenderContext = new WorldRenderContext(matrixStack, world, immediate, tickCounter, worldRenderState.cameraRenderState, profiler);
			WorldRenderEvent.EVENT.invoker().onRenderWorld(worldRenderContext);
			immediate.draw();
			RenderSystem.outputColorTextureOverride = null;
			RenderSystem.outputDepthTextureOverride = null;
			this.checkEmpty(matrixStack);
		});
	}

	@WrapOperation(method = "fillEntityRenderStates", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;getAndUpdateRenderState(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/entity/state/EntityRenderState;"))
	private EntityRenderState shouldMobGlow(WorldRenderer instance, Entity entity, float tickProgress, Operation<EntityRenderState> original) {
		EntityRenderState renderState = original.call(instance, entity, tickProgress);
		boolean shouldGlow = MobGlow.INSTANCE.hasOrCompute(entity);
		if (shouldGlow)
			renderState.outlineColor = MobGlow.INSTANCE.getMobGlowOrDefault(entity, renderState.outlineColor);
		return renderState;
	}
}
