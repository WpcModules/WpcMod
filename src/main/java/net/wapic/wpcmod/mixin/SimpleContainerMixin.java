package net.wapic.wpcmod.mixin;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.wapic.wpcmod.events.ReplaceItemEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleContainer.class)
public class SimpleContainerMixin {

	@Shadow
	@Final
	public NonNullList<ItemStack> items;

	@Inject(method = "getItem", at = @At("HEAD"), cancellable = true)
	public void getStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
		ItemStack[] stacks = this.items.toArray(new ItemStack[0]);
		ReplaceItemEvent.EVENT.invoker().onItemReplaced(stacks, slot, cir);
	}
}
