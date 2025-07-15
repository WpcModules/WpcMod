package net.wapic.wpcmod.features.general

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.screen.slot.Slot
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.util.ItemUtils.getSearchName
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.awt.Color

class DiscardHighlighter {

    private val config get() = WpcMod.config.generalConfig.discardSettings

    init {
        GuiEvents.DRAW_SLOT_BACKGROUND.register(::onDrawSlot)
    }

    fun onDrawSlot(drawContext: DrawContext, slot: Slot, callbackInfo: CallbackInfo) {
        if(!Screen.hasControlDown() || !config.discardHighlighter) return
        val search = config.discardRegex.toRegex()

        if(search.matches(slot.stack.getSearchName())) {
            drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Color(255, 0, 0).rgb)
        }
    }
}