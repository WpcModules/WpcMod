package net.wapic.wpcmod.features.dungeons

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.Items
import net.minecraft.screen.slot.Slot
import net.wapic.wpcmod.config.ConfigManager
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.util.ItemUtils
import java.awt.Color

class DiscardHighlighter {

    private val config get() = ConfigManager.config.dungeonConfig
    private val junkMatcher = "(?<enchantmentName>Bank|No Pain No Gain|Combo|Feather Falling|Infinite Quiver|Ultimate Jerry) (?<level>I|II|III|IV|V)".toRegex()

    init {
        GuiEvents.DRAW_SLOT_EVENT.register(::onDrawSlot)
    }

    fun onDrawSlot(drawContext: DrawContext, slot: Slot){
        if(!Screen.hasControlDown() && !config.discardHighlighter) return
        if(slot.stack.item == Items.ENCHANTED_BOOK) {
            val lore = ItemUtils.getLore(slot.stack)
            val enchantmentName = lore.first().string.trim()

            if(enchantmentName.matches(junkMatcher)){
                drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Color(255, 0, 0).rgb)
            }
        }
    }
}