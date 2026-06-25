package net.wapic.wpcmod.mixin;

import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.wapic.wpcmod.events.TooltipEvents;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultTooltipPositioner.class)
public class DefaultTooltipPositionerMixin {

	@Inject(method = "positionTooltip(IILorg/joml/Vector2i;II)V", at = @At(value = "HEAD"), cancellable = true)
	public void getPosition(int screenWidth, int screenHeight, Vector2i result, int tooltipWidth, int tooltipHeight, CallbackInfo ci) {
		TooltipEvents.POSITION.invoker().onPositionTooltip(screenWidth, screenHeight, result, tooltipWidth, tooltipHeight, ci);
	}
}
