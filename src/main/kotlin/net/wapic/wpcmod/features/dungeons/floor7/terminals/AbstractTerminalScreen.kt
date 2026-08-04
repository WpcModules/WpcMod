package net.wapic.wpcmod.features.dungeons.floor7.terminals

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.MenuAccess
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.CommonColors
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.render.drawRoundedRect

abstract class AbstractTerminalScreen(protected val type: TerminalType, initialMenu: ChestMenu, title: Component) :
	Screen(title), ContainerListener, MenuAccess<ChestMenu> {
	protected val config get() = WpcMod.config.dungeon.floor7.terminalSolvers
	private var menu: ChestMenu = initialMenu

	private val scaledSlotSize get() = 16 * config.customTermSize
	protected val totalSlotSpace = scaledSlotSize + config.gap * config.customTermSize

	private val slotMap: MutableMap<Slot, Box> = mutableMapOf()
	protected val items = arrayOfNulls<Slot>(type.windowSize)
	private var allowClick: Boolean = true

	init {
		menu.addSlotListener(this)
	}

	override fun init() {
		this.width =
			(MC.font.width(title) * config.customTermSize).coerceAtLeast((type.width + 0.5f) * totalSlotSpace).toInt()
		this.height = (menu.rowCount * totalSlotSpace).toInt()
	}

	override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
		getHoveredSlot(event.x, event.y)?.let { slot ->
			return slotClicked(slot, event.button())
		}
		return false
	}

	open fun slotClicked(slot: Slot, button: Int): Boolean = false

	protected fun doTerminalClick(slot: Slot, button: Int): Boolean {
		if (!allowClick) return false
		val input = if (button == 0 || button == 1) ContainerInput.PICKUP else ContainerInput.CLONE

		MC.clickSlot(menu.containerId, slot.index, button, input)
		MC.playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1f)

		allowClick = false
		WpcMod.LOGGER.debug("Clicked slot {} with button {}", slot.index, button)
		return true
	}

	override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
		extractSlots(graphics)
	}

	override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, deltaTicks: Float) {
		val pose = graphics.pose()
		pose.pushMatrix()
		val x = (graphics.guiWidth() - width) / 2f
		val y = (graphics.guiHeight() - height) / 2f
		pose.translate(x, y)
		graphics.drawRoundedRect(0, 0, this.width, this.height, config.backgroundRoundness, config.backgroundColor)
		extractTitle(graphics)
		pose.popMatrix()
	}

	private fun extractTitle(graphics: GuiGraphicsExtractor) {
		val pose = graphics.pose()
		pose.pushMatrix()
		pose.translate(this.width / 2f, MC.font.lineHeight / 2f)
		pose.scale(config.customTermSize / 1.25f)
		graphics.centeredText(MC.font, title, 0, 0, CommonColors.WHITE)
		pose.popMatrix()
	}

	abstract fun extractSlots(graphics: GuiGraphicsExtractor)

	protected fun extractSlot(graphics: GuiGraphicsExtractor, slot: Slot, color: ChromaColour, text: String = "") {
		val pose = graphics.pose()

		val x = (slot.index % 9 - 4) * totalSlotSpace + (graphics.guiWidth() - scaledSlotSize) / 2
		val y = (slot.index / 9 - 2) * totalSlotSpace + (graphics.guiHeight() - scaledSlotSize) / 2

		slotMap[slot] = Box(x, y, scaledSlotSize)

		pose.pushMatrix()
		pose.translate(x, y)
		pose.scale(config.customTermSize)
		graphics.drawRoundedRect(0, 0, 16, 16, config.slotRoundness, color)
		graphics.centeredText(MC.font, text, 8, MC.font.lineHeight / 2, CommonColors.WHITE)
		if (config.debug) graphics.item(slot.item, 0, 0)
		pose.popMatrix()
	}

	override fun getMenu(): ChestMenu = menu

	override fun onClose() {
		super.onClose()
		MC.player?.closeContainer()
	}

	override fun removed() {
		val player = MC.player ?: return
		menu.removed(player)
		menu.removeSlotListener(this)
	}

	protected fun getHoveredSlot(mouseX: Double, mouseY: Double): Slot? {
		slotMap.entries.find { isHovering(it.value.x, it.value.y, it.value.l, mouseX, mouseY) }?.let {
			return it.key
		}
		return null
	}

	private fun isHovering(slotX: Float, slotY: Float, length: Float, mouseX: Double, mouseY: Double): Boolean {
		return mouseX in slotX..(slotX + length) && mouseY in slotY..(slotY + length)
	}

	fun changeHandler(newMenu: ChestMenu) {
		menu.removeSlotListener(this)
		menu = newMenu
		menu.addSlotListener(this)
	}

	override fun dataChanged(container: AbstractContainerMenu, id: Int, value: Int) = Unit
	override fun slotChanged(container: AbstractContainerMenu, slotIndex: Int, itemStack: ItemStack) {
		if (slotIndex >= type.windowSize) {
			allowClick = true
			return
		}
		if (itemStack.item == Items.BLACK_STAINED_GLASS_PANE) return

		val slot = menu.getSlot(slotIndex)
		items[slot.index] = slot
		WpcMod.LOGGER.debug("Slot changed: {}, {}, {}", slotIndex, itemStack, itemStack.hoverName.string)
	}

	private data class Box(val x: Float, val y: Float, val l: Float)
}