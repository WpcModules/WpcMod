package net.wapic.wpcmod.features.inventory.experiments

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.events.ReplaceItemEvent
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.isSimilar
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object SuperpairsSolver {

	private val config get() = WpcMod.config.inventory.experiments

	/** REGEX-TEST: Superpairs (Metaphysical) */
	private val superpairsTitle = Regex("Superpairs ?\\(.+\\)")
	private var inSuperpairs: Boolean = false

	private val powerUps = listOf(Items.DIAMOND, Items.FEATHER, Items.LAPIS_BLOCK)
	private val ignoredItems = listOf(Items.CLOCK, Items.BOOKSHELF, Items.STAINED_GLASS_PANE.black, Items.CAULDRON)

	private val superpairsMap = mutableMapOf<Int, ItemStack>()
	private val slotsToRead = mutableSetOf<Slot>()

	private val foundPairs = mutableSetOf<Int>()
	private var lastClickedSlot: Slot? = null

	private var itemToInstantFind: Slot? = null
	private var activeInstantFinds: Int = 0

	private val skyHanniRegex = Regex("\\?|(?:Click a(?: seco)?n[dy]|Next) button(?: is instantly rewarded)?!?")

	fun init() {
		GuiEvents.OPEN.register(::onInventoryOpen)
		GuiEvents.SLOT_UPDATE_AFTER.register(::onSlotUpdate)
		GuiEvents.SLOT_CLICKED.register(::onMouseClick)
		GuiEvents.DRAW_SLOT_BACKGROUND.register(::onDrawSlot)
		GuiEvents.CLOSE.register(::onInventoryClosed)

		ReplaceItemEvent.EVENT.register(::onReplaceItem)
	}

	fun onInventoryOpen(title: String, containerId: Int) {
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
		slotActionType: ContainerInput,
		callbackInfo: CallbackInfo
	) {
		if (!inSuperpairs || !config.superpairsSolver) return
		if (slot?.container is Inventory || slot?.containerSlot in foundPairs || slot?.item?.item in ignoredItems) return

		slot?.let { slot ->
			if (slotId !in superpairsMap.keys) {
				if (skyHanniRegex.matches(slot.item.hoverName.string)) {
					slotsToRead.add(slot)
					return
				}
				if (slot.item.item != Items.AIR) superpairsMap[slotId] = slot.item
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
			foundPairs.add(slot.containerSlot)
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

		slotsToRead.find { it.containerSlot == slotId && it.item.item != Items.AIR && it.containerSlot !in foundPairs }?.let { slot ->
			superpairsMap[slotId] = itemStack
			checkForPair(slot)
			slotsToRead.removeIf { it.containerSlot == slotId }
		}
	}

	fun onDrawSlot(drawContext: GuiGraphicsExtractor, screen: Screen, slot: Slot, callbackInfo: CallbackInfo) {
		if (slot.container is Inventory || !inSuperpairs || !config.superpairsSolver) return
		if (slot.item.item in ignoredItems || skyHanniRegex.matches(slot.item.hoverName.string)) return

		val inv = MC.player?.containerMenu?.items

		val color: ChromaColour? = when {
			slot.item.item in powerUps -> config.superpairColors.powerUp
			slot.containerSlot in foundPairs -> config.superpairColors.foundPair
			else -> inv?.count { it.isSimilar(slot.item) }?.let {
				if(it > 1)
					config.superpairColors.discoveredPair
				else
					config.superpairColors.undiscoveredPair
			}
		}

		color?.let { drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, it.getEffectiveColourRGB()) }
	}
}