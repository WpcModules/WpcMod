package net.wapic.wpcmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;
import net.wapic.wpcmod.events.WorldRenderEvent;
import net.wapic.wpcmod.features.entity.MobGlow;
import net.wapic.wpcmod.features.general.Freecam;
import net.wapic.wpcmod.util.render.WorldRenderContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
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
	private DefaultFramebufferSet framebufferSet;

	@Shadow
	@Final
	private BufferBuilderStorage bufferBuilders;

	@Inject(method = "renderMain", at = @At("TAIL"))
	private void onRenderWorld(FrameGraphBuilder frameGraphBuilder, Frustum frustum, Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, Fog fog, boolean renderBlockOutline, boolean renderEntityOutlines, RenderTickCounter renderTickCounter, Profiler profiler, CallbackInfo ci) {
		if(world == null || client.player == null) return;

		FramePass framePass = frameGraphBuilder.createPass("wpcmod:render_world");

		this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);

		Handle<Framebuffer> handle = this.framebufferSet.mainFramebuffer;
		framePass.setRenderer(() -> {
			RenderSystem.setShaderFog(fog);
			MatrixStack matrixStack = new MatrixStack();
			VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
			WorldRenderContext worldRenderContext = new WorldRenderContext(matrixStack, world, immediate, renderTickCounter, camera, profiler);
			WorldRenderEvent.EVENT.invoker().onRenderWorld(worldRenderContext);
			immediate.draw();
			this.checkEmpty(matrixStack);
		});
	}

	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"), method = {"getEntitiesToRender", "renderEntities"}, require = 2)
	private boolean shouldMobGlow(boolean original, @Local Entity entity) {
		boolean shouldGlow = MobGlow.INSTANCE.hasOrCompute(entity);
		return shouldGlow || original;
	}

	@Redirect(method = "getEntitiesToRender", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;", ordinal = 3))
	private Entity freecam_RenderPlayer(Camera camera) {
		if (Freecam.Companion.isEnabled()) {
			return MinecraftClient.getInstance().player;
		}
		return camera.getFocusedEntity();
	}

	@SuppressWarnings({"InvalidInjectorMethodSignature", "MixinAnnotationTarget"})
	@ModifyVariable(method = "renderEntities",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;setColor(IIII)V")),
			at = @At("STORE"), ordinal = 0
	)
	private int modifyGlowColor(int color, @Local Entity entity) {
		return MobGlow.INSTANCE.getMobGlowOrDefault(entity, color);
	}
}
