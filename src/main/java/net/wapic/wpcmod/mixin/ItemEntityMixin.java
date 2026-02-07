package net.wapic.wpcmod.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.wapic.wpcmod.events.EntityEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

	@Unique
	private int lastItem = -1;

	@Shadow
	public abstract ItemStack getItem();

	@Inject(method = "onSyncedDataUpdated", at = @At("RETURN"))
	private void onStackSynced(EntityDataAccessor<?> entityDataAccessor, CallbackInfo ci) {
		ItemStack stack = this.getItem();

		if (stack != null && stack.is(Items.PLAYER_HEAD)) {
			var profile = stack.get(DataComponents.PROFILE);
			if (profile != null) {
				if (profile.hashCode() == lastItem) return;
				lastItem = profile.hashCode();

				EntityEvents.ITEM_DATA_SET.invoker().onEntitySetItemData(stack);
			}
		}
	}
}