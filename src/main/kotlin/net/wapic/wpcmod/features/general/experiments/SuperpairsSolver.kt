package net.wapic.wpcmod.features.general.experiments

import net.minecraft.client.MinecraftClient
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

    private val foundPairs = mutableSetOf<Int>()
    private val potentialPair = mutableSetOf<Int>()

    private var activeInstantFinds: Int = 0
    private var itemToInstantFind: ItemStack? = null

    /**
     * REGEX-TEST: ?
     * REGEX-TEST: Click any button!
     * REGEX-TEST: Click a second button!
     * REGEX-TEST: Next button is instantly rewarded!
     */
    private val skyHanniRegex = "\\?|(?:Click a(?: seco)?n[dy]|Next) button(?: is instantly rewarded)?!?".toRegex()

    init {
        GuiEvents.SLOT_CLICKED.register(::onMouseClick)
        GuiEvents.DRAW_SLOT_BACKGROUND.register(::onDrawSlot)

        ReplaceItemEvent.EVENT.register(::onReplaceItem)

        InventoryEvents.OPEN.register(::onInventoryOpen)
        InventoryEvents.CLOSE.register(::onInventoryClosed)
        InventoryEvents.SLOT_UPDATE.register(::onSlotUpdate)
    }

    fun onInventoryOpen(title: String) {
        if(!config.superpairsSolver) return
        inSuperpairs = title.matches(superpairsTitle)
    }

    fun onInventoryClosed() {
        inSuperpairs = false
        superpairsMap.clear()
        slotsToRead.clear()
        potentialPair.clear()
        foundPairs.clear()
        activeInstantFinds = 0
        itemToInstantFind = null
    }

    fun onReplaceItem(inventoryContents: Array<ItemStack>, slot: Int, callbackInfoReturnable: CallbackInfoReturnable<ItemStack>) {
        if(!config.superpairsSolver || !inSuperpairs) return
        if(superpairsMap.isEmpty() || slot !in superpairsMap.keys) return
        val replacementItem = superpairsMap[slot] ?: return
        callbackInfoReturnable.returnValue = replacementItem
    }

    fun onMouseClick(slot: Slot, slotId: Int, button: Int, slotActionType: SlotActionType, callbackInfo: CallbackInfo) {
        if(slot.inventory is PlayerInventory || !inSuperpairs || !config.superpairsSolver) return


        if(slotId !in superpairsMap.keys) {
            if (skyHanniRegex.matches(slot.stack.name.string)) {
                slotsToRead.add(slotId)
                println("adding slot to read: $slotId")
            } else {
                println("slotClick, mapping slot: $slotId to ${slot.stack.name.string}")
                superpairsMap[slotId] = slot.stack
                println("slotClick, checkForPairs: $slotId, ${slot.stack.name.string}")
                checkForPair(slotId, slot.stack)
            }
        } else {
            if(activeInstantFinds > 0) {
               itemToInstantFind = slot.stack
                println("slotClick, activeInstantFinds: $activeInstantFinds, ${itemToInstantFind?.name?.string}")
            }
        }
    }

    fun handleInstantFind(itemStack: ItemStack) {
        val screenHandler = MinecraftClient.getInstance().player?.currentScreenHandler ?: return
        val items = screenHandler.slots.filter { it.stack.name == itemStack.name && it.stack.item == itemStack.item }.map { it.index }
        println("handledInstantFind result: ${items.joinToString { "$it" }}")
        foundPairs.addAll(items)
        itemToInstantFind = null
        activeInstantFinds--
    }

    fun checkForPair(slotId: Int, itemStack: ItemStack) {
        if(itemStack.item == Items.DIAMOND) {
            activeInstantFinds++
            potentialPair.clear()
            println("found InstantFind: $activeInstantFinds")
        }
        if(itemStack.item in powerUps) return

        if(potentialPair.isEmpty()) {
            potentialPair.add(slotId)
            println("added: $slotId")
            return
        }

        val screenHandler = MinecraftClient.getInstance().player?.currentScreenHandler ?: return
        val item = screenHandler.getSlot(potentialPair.first())
        if(item.stack.name == itemStack.name && item.stack.item == itemStack.item) {
            println("found matching pair: $slotId with ${itemStack.name.string} -> ${potentialPair.first()} with ${item.stack.name.string}")
            foundPairs.addAll(listOf(item.index, slotId))
        } else {
            println("no matching pair found for $slotId")
        }
        println("resetting clicks")
        potentialPair.clear()
    }

    fun onSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack) {
        if(!inSuperpairs || !config.superpairsSolver) return
        if(slotId > 53 || itemStack.isEmpty) return
        if(skyHanniRegex.matches(itemStack.name.string)) return
        itemToInstantFind?.let {
            println("itemToInstantFind: ${itemToInstantFind?.name?.string}")
            if(itemStack.name == it.name && itemStack.item == it.item) handleInstantFind(itemStack)
        }

        if(slotsToRead.isNotEmpty() && slotId in slotsToRead) {
            superpairsMap[slotId] = itemStack
            println("slotUpdate, mapping slot: $slotId to ${itemStack.name.string}")
            if(activeInstantFinds > 0) {
                println("slotUpdate, activeInstantFinds: $activeInstantFinds, ${itemStack.name.string}")
                handleInstantFind(itemStack)
            } else {
                println("slotUpdate, checkForPairs: $slotId, ${itemStack.name.string}")
                checkForPair(slotId, itemStack)
            }
            slotsToRead.removeIf { it == slotId }
        }
    }

    fun onDrawSlot(drawContext: DrawContext, slot: Slot, callbackInfo: CallbackInfo) {
        if(slot.inventory is PlayerInventory || !inSuperpairs || !config.superpairsSolver) return
        if(skyHanniRegex.matches(slot.stack.name.string)) return


        val inv = MinecraftClient.getInstance().player?.currentScreenHandler?.stacks
        inv?.count { it.name == slot.stack.name && it.item == slot.stack.item }?.let {
            val color = if(it > 1) Color(255, 69, 0, 150) else Color(240,230,140)
            drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, color.rgb)
        }

        if(slot.index in foundPairs) drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Color.GREEN.rgb)
        if(slot.stack.item in powerUps) drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Color(100,30,130).rgb)
    }
}