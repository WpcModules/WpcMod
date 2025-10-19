package net.wapic.wpcmod.features.inventory.experiments

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
import net.wapic.wpcmod.util.ItemUtils.isSimilar
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.awt.Color

class SuperpairsSolver {

	private val config get() = WpcMod.config.inventory.experiments

	/** REGEX-TEST: Superpairs (Metaphysical) */
	private val superpairsTitle = Regex("Superpairs ?\\(.+\\)")
	private var inSuperpairs: Boolean = false

	private val powerUps = listOf<Item>(Items.DIAMOND, Items.FEATHER, Items.LAPIS_BLOCK)
	private val ignoredItems =
		listOf<Item>(Items.CLOCK, Items.BOOKSHELF, Items.BLACK_STAINED_GLASS_PANE, Items.CAULDRON)

	private val superpairsMap = mutableMapOf<Int, ItemStack>()
	private val slotsToRead = mutableSetOf<Slot>()

	private val foundPairs = mutableSetOf<Int>()
	private var lastClickedSlot: Slot? = null

	private var itemToInstantFind: Slot? = null
	private var activeInstantFinds: Int = 0

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
		if (!config.superpairsSolver) return
		inSuperpairs = title.matches(superpairsTitle)
	}

	fun onInventoryClosed() {
		inSuperpairs = false
		superpairsMap.clear()
		slotsToRead.clear()
		foundPairs.clear()
		activeInstantFinds = 0
		itemToInstantFind = null
		lastClickedSlot = null
	}

	fun onReplaceItem(
		inventoryContents: Array<ItemStack>, slot: Int, callbackInfoReturnable: CallbackInfoReturnable<ItemStack>
	) {
		if (!config.superpairsSolver || !inSuperpairs) return
		if (superpairsMap.isEmpty() || slot !in superpairsMap.keys) return
		val replacementItem = superpairsMap[slot] ?: return
		callbackInfoReturnable.returnValue = replacementItem
	}

	fun onMouseClick(slot: Slot, slotId: Int, button: Int, slotActionType: SlotActionType, callbackInfo: CallbackInfo) {
		if (slot.inventory is PlayerInventory || !inSuperpairs || !config.superpairsSolver || slot.stack.item in ignoredItems) return

		if (slotId !in superpairsMap.keys) {
			if (skyHanniRegex.matches(slot.stack.name.string)) {
				slotsToRead.add(slot)
				return
			}
			superpairsMap[slotId] = slot.stack
		}
		if (lastClickedSlot?.index != slotId) checkForPair(slot)
	}

	fun hasPair(itemStack: ItemStack): Boolean {
		val screenHandler = MinecraftClient.getInstance().player?.currentScreenHandler ?: return false
		val items = screenHandler.slots.filter { itemStack.isSimilar(it.stack) }.map { it.index }
		if (items.size == 2) {
			foundPairs.addAll(items)
			activeInstantFinds--
			return true
		}
		return false
	}

	fun checkForPair(slot: Slot) {
		if (slot.stack.item !in powerUps && activeInstantFinds > 0) {
			if (hasPair(slot.stack)) {
				return
			}
			itemToInstantFind = slot
			return
		}

		if (slot.stack.item == Items.DIAMOND) {
			activeInstantFinds++
			lastClickedSlot = null
			return
		}

		if (slot.stack.item in powerUps) return

		if (lastClickedSlot == null) {
			lastClickedSlot = slot
			return
		}

		lastClickedSlot?.let {
			if (it.stack.isSimilar(slot.stack)) {
				foundPairs.addAll(listOf(it.index, slot.index))
			}
		}

		lastClickedSlot = null
	}

	fun onSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack) {
		if (!inSuperpairs || !config.superpairsSolver) return
		if (slotId > 53 || itemStack.isEmpty) return
		if (skyHanniRegex.matches(itemStack.name.string)) return
		itemToInstantFind?.let {
			if (it.stack.isSimilar(itemStack) && it.index != slotId) {
				if (hasPair(itemStack)) itemToInstantFind = null
			}
		}

		slotsToRead.find { it.index == slotId }?.let { slot ->
			superpairsMap[slotId] = itemStack
			checkForPair(slot)
			slotsToRead.removeIf { it.index == slotId }
		}
	}

	fun onDrawSlot(drawContext: DrawContext, slot: Slot, callbackInfo: CallbackInfo) {
		if (slot.inventory is PlayerInventory || !inSuperpairs || !config.superpairsSolver) return
		if (slot.stack.item in ignoredItems || skyHanniRegex.matches(slot.stack.name.string)) return

		val inv = MinecraftClient.getInstance().player?.currentScreenHandler?.stacks
		inv?.count { it.isSimilar(slot.stack) }?.let {
			val color = if (it > 1) Color(255, 69, 0, 150) else Color(240, 230, 140)
			drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, color.rgb)
		}

		if (slot.index in foundPairs) drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Color.GREEN.rgb)
		if (slot.stack.item in powerUps) drawContext.fill(
			slot.x, slot.y, slot.x + 16, slot.y + 16, Color(100, 30, 130).rgb
		)
	}
}