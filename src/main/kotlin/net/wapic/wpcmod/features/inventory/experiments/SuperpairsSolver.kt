package net.wapic.wpcmod.features.inventory.experiments

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.ClickType
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.events.ReplaceItemEvent
import net.wapic.wpcmod.util.ItemUtils.isSimilar
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.awt.Color

object SuperpairsSolver {

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

	private val skyHanniRegex = Regex("\\?|(?:Click a(?: seco)?n[dy]|Next) button(?: is instantly rewarded)?!?")

	fun init() {
		GuiEvents.OPEN.register(::onInventoryOpen)
		GuiEvents.SLOT_UPDATE.register(::onSlotUpdate)
		GuiEvents.SLOT_CLICKED.register(::onMouseClick)
		GuiEvents.DRAW_SLOT_BACKGROUND.register(::onDrawSlot)
		GuiEvents.CLOSE.register(::onInventoryClosed)

		ReplaceItemEvent.EVENT.register(::onReplaceItem)
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

	fun onMouseClick(
		slot: Slot?,
		slotId: Int,
		button: Int,
		slotActionType: ClickType,
		callbackInfo: CallbackInfo
	) {
		if (slot?.container is Inventory || !inSuperpairs || !config.superpairsSolver || slot?.item?.item in ignoredItems) return

		slot?.let { slot ->
			if (slotId !in superpairsMap.keys) {
				if (skyHanniRegex.matches(slot.item.hoverName.string)) {
					slotsToRead.add(slot)
					return
				}
				superpairsMap[slotId] = slot.item
			}
			if (lastClickedSlot?.containerSlot != slotId) checkForPair(slot)
		}
	}

	fun hasPair(itemStack: ItemStack): Boolean {
		val screenHandler = Minecraft.getInstance().player?.containerMenu ?: return false
		val items = screenHandler.slots.filter { itemStack.isSimilar(it.item) }.map { it.containerSlot }
		if (items.size == 2) {
			foundPairs.addAll(items)
			activeInstantFinds--
			return true
		}
		return false
	}

	fun checkForPair(slot: Slot) {
		if (slot.item.item !in powerUps && activeInstantFinds > 0) {
			if (hasPair(slot.item)) {
				return
			}
			itemToInstantFind = slot
			return
		}

		if (slot.item.item == Items.DIAMOND) {
			activeInstantFinds++
			lastClickedSlot = null
			return
		}

		if (slot.item.item in powerUps) return

		if (lastClickedSlot == null) {
			lastClickedSlot = slot
			return
		}

		lastClickedSlot?.let {
			if (it.item.isSimilar(slot.item)) {
				foundPairs.addAll(listOf(it.containerSlot, slot.containerSlot))
			}
		}

		lastClickedSlot = null
	}

	fun onSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack) {
		if (!inSuperpairs || !config.superpairsSolver) return
		if (slotId > 53 || itemStack.isEmpty) return
		if (skyHanniRegex.matches(itemStack.hoverName.string)) return
		itemToInstantFind?.let {
			if (it.item.isSimilar(itemStack) && it.containerSlot != slotId) {
				if (hasPair(itemStack)) itemToInstantFind = null
			}
		}

		slotsToRead.find { it.containerSlot == slotId }?.let { slot ->
			superpairsMap[slotId] = itemStack
			checkForPair(slot)
			slotsToRead.removeIf { it.containerSlot == slotId }
		}
	}

	fun onDrawSlot(drawContext: GuiGraphics, screen: Screen, slot: Slot, callbackInfo: CallbackInfo) {
		if (slot.container is Inventory || !inSuperpairs || !config.superpairsSolver) return
		if (slot.item.item in ignoredItems || skyHanniRegex.matches(slot.item.hoverName.string)) return

		val inv = Minecraft.getInstance().player?.containerMenu?.items
		inv?.count { it.isSimilar(slot.item) }?.let {
			val color = if (it > 1) Color(255, 69, 0, 150) else Color(240, 230, 140)
			drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, color.rgb)
		}

		if (slot.containerSlot in foundPairs) drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Color.GREEN.rgb)
		if (slot.item.item in powerUps) drawContext.fill(
			slot.x, slot.y, slot.x + 16, slot.y + 16, Color(100, 30, 130).rgb
		)
	}
}