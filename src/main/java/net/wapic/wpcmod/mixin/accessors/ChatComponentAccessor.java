package net.wapic.wpcmod.mixin.accessors;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatComponent.class)
public interface ChatComponentAccessor {

	@Accessor("allMessages")
	List<GuiMessage> getAllMessages();

	@Accessor("trimmedMessages")
	List<GuiMessage.Line> getTrimmedMessages();
}
