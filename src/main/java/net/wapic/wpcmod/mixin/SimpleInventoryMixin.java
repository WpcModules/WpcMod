package net.wapic.wpcmod.mixin;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.wapic.wpcmod.events.ReplaceItemEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleInventory.class)
public class SimpleInventoryMixin {

	@Shadow
	@Final
	public DefaultedList<ItemStack> heldStacks;

	@Inject(method = "getStack", at = @At("HEAD"), cancellable = true)
	public void getStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
		ItemStack[] stacks = this.heldStacks.toArray(new ItemStack[0]);
		ReplaceItemEvent.EVENT.invoker().onItemReplaced(stacks, slot, cir);
	}
}
