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
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.drawRoundedRect

abstract class AbstractTerminalScreen(initialMenu: ChestMenu, title: Component) :
	Screen(title), MenuAccess<ChestMenu>, ContainerListener {

	private var menu: ChestMenu = initialMenu
	private val slotMap: MutableMap<Int, Box> = mutableMapOf()
	val isSimulator = menu.containerId == Int.MAX_VALUE
	private val scaledSlotSize = 16 * config.customTermSize

	private var allowClick = false
	private var inventoryUpdated = false

	protected val totalSlotSpace = scaledSlotSize + config.gap * config.customTermSize
	protected val config get() = WpcMod.config.dungeon.floor7.terminalSolvers
	protected val solution = mutableSetOf<Int>()

	init {
		menu.addSlotListener(this)
	}

	override fun tick() {
		super.tick()
		Terminal.handler?.onTick()

		if(inventoryUpdated) {
			solution.clear()
			val slots = menu.slots.subList(0, menu.container.containerSize)
			onInventoryUpdated(slots)
			inventoryUpdated = false
			allowClick = true
			DungeonEvents.TERMINAL_UPDATED.invoker().onUpdate(this, slots)
		}
	}

	override fun init() {
		Terminal.handler?.create()
		DungeonEvents.TERMINAL_OPENED.invoker().onOpen(this)
		this.resize(width, height)
	}

	override fun resize(width: Int, height: Int) {
		this.width = width
		this.height = height
	}

	override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
		val slot = getHoveredSlot(event.x, event.y) ?: return false
		return slotClicked(slot, event.button())
	}

	abstract fun slotClicked(slotIndex: Int, button: Int): Boolean

	protected fun doTerminalClick(slotIndex: Int, button: Int): Boolean {
		if (!allowClick) return false
		allowClick = false
		val input = if (button.equalsOneOf(0, 1)) ContainerInput.PICKUP else ContainerInput.CLONE

		Terminal.handler?.slotClicked(menu.getSlot(slotIndex), slotIndex, button, input)
		if(!isSimulator) MC.clickSlot(menu.containerId, slotIndex, button, input)
		WpcMod.LOGGER.debug("Clicked terminal slot {}, button {}", slotIndex, button)

		MC.playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1f)
		DungeonEvents.TERMINAL_CLICKED.invoker().onClick(this, slotIndex, button)
		return true
	}

	override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
		extractSlots(graphics)
	}

	override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, deltaTicks: Float) {
		val pose = graphics.pose()
		pose.pushMatrix()
		val x = (graphics.guiWidth() - this.width) / 2f
		val y = (graphics.guiHeight() - this.height) / 2f
		pose.translate(x, y)
		graphics.drawRoundedRect(0, 0, this.width, this.height, config.backgroundRoundness, config.backgroundColor)
		extractTitle(graphics)
		pose.popMatrix()
	}

	private fun extractTitle(graphics: GuiGraphicsExtractor) {
		val pose = graphics.pose()
		pose.pushMatrix()
		pose.translate(this.width / 2f, font.lineHeight / 2f)
		pose.scale(config.customTermSize / 1.25f)

		graphics.centeredText(font, title, 0, 0, CommonColors.WHITE)

		pose.popMatrix()
	}

	abstract fun extractSlots(graphics: GuiGraphicsExtractor)

	protected fun extractSlot(graphics: GuiGraphicsExtractor, slotIndex: Int, color: ChromaColour, text: String = "") {

		val x = (slotIndex % 9 - 4) * totalSlotSpace + (graphics.guiWidth() - totalSlotSpace) / 2
		val y = (slotIndex / 9 - 2) * totalSlotSpace + (graphics.guiHeight() - totalSlotSpace) / 2

		slotMap[slotIndex] = Box(x, y, scaledSlotSize)

		val pose = graphics.pose()
		pose.pushMatrix()
		pose.translate(x, y)
		pose.scale(config.customTermSize)

		graphics.drawRoundedRect(0, 0, 16, 16, config.slotRoundness, color)
		graphics.centeredText(font, text, 8, font.lineHeight / 2, CommonColors.WHITE)
		if (config.debug) {
			graphics.fakeItem(menu.getSlot(slotIndex).item, 0, 0)
			graphics.itemDecorations(font, menu.getSlot(slotIndex).item, 0, 0)
		}

		pose.popMatrix()
	}

	override fun getMenu(): ChestMenu = menu

	override fun onClose() {
		if(isSimulator) {
			minecraft.player?.clientSideCloseContainer()
		} else {
			minecraft.player?.closeContainer()
		}
		DungeonEvents.TERMINAL_CLOSED.invoker().onClose()
	}

	override fun removed() {
		val player = minecraft.player ?: return
		menu.removed(player)
		menu.removeSlotListener(this)
		Terminal.handler?.removed()
	}

	protected fun getHoveredSlot(mouseX: Double, mouseY: Double): Int? {
		return slotMap.entries.find { isHovering(it.value.x, it.value.y, it.value.l, mouseX, mouseY) }?.key
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
		inventoryUpdated = true
	}

	abstract fun onInventoryUpdated(slots: List<Slot>)

	private data class Box(val x: Float, val y: Float, val l: Float)
}