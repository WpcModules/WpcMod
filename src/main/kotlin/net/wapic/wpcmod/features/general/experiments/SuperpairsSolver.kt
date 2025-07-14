package net.wapic.wpcmod.features.general.experiments

import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.events.InventoryEvents
import net.wapic.wpcmod.events.ReplaceItemEvent
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.awt.Color

class SuperpairsSolver {
    private val config get() = WpcMod.config.generalConfig.experimentSettings

    /** REGEX-TEST: Superpairs (Metaphysical) */
    private val superpairsTitle = "Superpairs ?\\(.+\\)".toRegex()
    private var inSuperpairs: Boolean = false

    private val powerUps = listOf<Item>(Items.DIAMOND, Items.FEATHER, Items.LAPIS_BLOCK)

    private val superpairsMap = mutableMapOf<Int, ItemStack>()
    private val slotsToRead = mutableSetOf<Int>()

    /**
     * REGEX-TEST: ?
     * REGEX-TEST: Click any button!
     * REGEX-TEST: Click a second button!
     * REGEX-TEST: Next button is instantly rewarded!
     */
    private val skyHanniRegex = "\\?|(?:Click a(?: seco)?n[dy]|Next) button(?: is instantly rewarded)?!?".toRegex()

    init {
        InventoryEvents.OPEN.register(::onInventoryOpen)
        GuiEvents.SLOT_CLICKED.register(::onMouseClick)
        GuiEvents.DRAW_SLOT_BACKGROUND.register(::onDrawSlot)

        ReplaceItemEvent.EVENT.register(::onReplaceItem)

        InventoryEvents.CLOSE.register(::onInventoryClosed)
        InventoryEvents.UPDATE.register(::onInventoryUpdate)
    }

    fun onInventoryUpdate(syncId: Int, slotId: Int, itemStack: ItemStack) {
        if(!inSuperpairs || !config.superpairsSolver) return
        if(slotId > 53 || itemStack.isEmpty) return
        if(skyHanniRegex.matches(itemStack.name.string)) return

        if(slotsToRead.isNotEmpty() && slotId in slotsToRead) {
            superpairsMap[slotId] = itemStack
            slotsToRead.removeIf { it == slotId }
        }
    }

    fun onInventoryOpen(title: String) {
        if(!config.superpairsSolver) return
        inSuperpairs = title.matches(superpairsTitle)
    }

    fun onReplaceItem(inventoryContents: Array<ItemStack>, slot: Int, callbackInfoReturnable: CallbackInfoReturnable<ItemStack>) {
        if(!config.superpairsSolver || !inSuperpairs) return
        if(superpairsMap.isEmpty() || slot !in superpairsMap.keys) return
        val replacementItem = superpairsMap[slot] ?: return
        callbackInfoReturnable.returnValue = replacementItem
    }

    fun onInventoryClosed() {
        inSuperpairs = false
        superpairsMap.clear()
        slotsToRead.clear()
    }

    fun onMouseClick(slot: Slot, slotId: Int, button: Int, slotActionType: SlotActionType, callbackInfo: CallbackInfo) {
        if (slot.inventory is PlayerInventory || !inSuperpairs || !config.superpairsSolver) return

        val slotNumber = slotId.takeIf { it !in superpairsMap.keys } ?: return

        if (skyHanniRegex.matches(slot.stack.name.string)) {
            slotsToRead.add(slotNumber)
        } else {
            superpairsMap[slotNumber] = slot.stack
        }
    }

    fun onDrawSlot(drawContext: DrawContext, slot: Slot, callbackInfo: CallbackInfo) {
        if(slot.inventory is PlayerInventory || !inSuperpairs || !config.superpairsSolver) return
        val mapCopy = superpairsMap.toMap()

        mapCopy.forEach { (slotNumber, itemStack) ->
            val count = superpairsMap.values.count { it.name == itemStack.name && it.item == itemStack.item }
            if(slotNumber == slot.index) {
                if(count > 1) {
                    drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Color(255, 69, 0, 150).rgb)
                } else {
                    drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Color(240,230,140).rgb)
                }
            }
        }

        if(slot.stack.item in powerUps) {
            drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Color(100,30,130).rgb)
        }
    }
}