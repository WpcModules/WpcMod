package net.wapic.wpcmod.mixin.accessors;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {

	@Invoker("extractSlot")
	void extractSlot_WpcMod(final GuiGraphicsExtractor graphics, final Slot slot, final int mouseX, final int mouseY);
}
