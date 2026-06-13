package net.wapic.wpcmod.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.wapic.wpcmod.events.EntityEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

	@Unique
	private int lastItem = -1;

	@Inject(method = "setItem", at = @At("RETURN"))
	private void onStackSynced(ItemStack itemStack, CallbackInfo ci) {
		if (itemStack != null && itemStack.is(Items.PLAYER_HEAD)) {
			var profile = itemStack.get(DataComponents.PROFILE);
			if (profile != null) {
				if (profile.hashCode() == lastItem) return;
				lastItem = profile.hashCode();

				EntityEvents.ITEM_DATA_SET.invoker().onEntitySetItemData(itemStack);
			}
		}
	}
}