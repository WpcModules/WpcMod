package net.wapic.wpcmod.features.dungeons.floor7.terminals

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.MenuAccess
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.util.Util
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.drawRoundedRect

abstract class AbstractTerminalScreen(initialMenu: ChestMenu, title: Component) : Screen(title), MenuAccess<ChestMenu> {
	abstract val gameWidth: Int
	abstract val gameHeight: Int

	private var menu: ChestMenu = initialMenu
	private var isInitialized = false
	private var hoveredSlot: Int? = null
	private var nextClickTime: Long = 0

	private val slotMap: MutableMap<Int, Box> = mutableMapOf()
	private val isSimulator = menu.containerId == Int.MAX_VALUE
	private val scaledSlotSize = 16 * config.customTermSize
	private val clickedSlots = mutableSetOf<Pair<Int, Long>>()

	protected val totalSlotSpace = scaledSlotSize + config.gap * config.customTermSize
	protected val slots get() = menu.slots.subList(0, menu.container.containerSize)
	protected val config get() = WpcMod.config.dungeon.floor7.terminalSolvers
	protected val solution = mutableListOf<Int>()

	override fun tick() {
		super.tick()
		Terminal.handler?.onTick()

		if (clickedSlots.isEmpty()) return

		val iterator = clickedSlots.iterator()
		while (iterator.hasNext()) {
			val slotToTime = iterator.next()
			if (slotToTime.second >= Util.getEpochMillis()) continue

			val misingItem = solveTerminal(listOf(menu.getSlot(slotToTime.first)))
			if (misingItem.isEmpty()) continue

			// this needs to be improved like wtf is this, also shouldn't number terminal break at some point???
			if (this is RubixTerminalScreen) {
				val index = listOf(12, 13, 14, 21, 22, 23, 30, 31, 32).indexOf(slotToTime.first)
				solution[index] = misingItem.first()
			} else {
				solution.add(misingItem.first())
			}

			iterator.remove()
			WpcMod.LOGGER.debug("Synchronized terminal with solution {}", solution)
		}
	}

	override fun init() {
		Terminal.handler?.create()
		DungeonEvents.TERMINAL_OPENED.invoker().onOpen(this)
	}

	override fun keyPressed(event: KeyEvent): Boolean {
		if (super.keyPressed(event)) return true

		if (nextClickTime >= Util.getEpochMillis()) return false

		if (minecraft.options.keyDrop.matches(event)) {
			return slotClicked(hoveredSlot ?: return false, 0, ContainerInput.THROW)
		}

		return false
	}

	override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
		if (nextClickTime >= Util.getEpochMillis()) return false

		val slot = getHoveredSlot(event.x, event.y) ?: return false
		val input = if (event.button().equalsOneOf(0, 1)) ContainerInput.PICKUP else ContainerInput.CLONE
		return slotClicked(slot, event.button(), input)
	}

	abstract fun slotClicked(slotIndex: Int, button: Int, input: ContainerInput): Boolean

	protected fun doTerminalClick(slotIndex: Int, button: Int, input: ContainerInput) {
		Terminal.handler?.slotClicked(menu.getSlot(slotIndex), slotIndex, button, input)

		if (!isSimulator) {
			MC.clickSlot(menu.containerId, slotIndex, button, input)
			clickedSlots.add(Pair(slotIndex, Util.getEpochMillis() + config.resyncTime.toLong()))
		}

		nextClickTime = Util.getEpochMillis() + config.clickDelay.toLong()
		DungeonEvents.TERMINAL_CLICKED.invoker().onClick(this, slotIndex, button)
		WpcMod.LOGGER.debug("Clicked terminal slot {}, button {}, {}", slotIndex, button, Util.getEpochMillis())
	}

	override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
		hoveredSlot = getHoveredSlot(mouseX.toDouble(), mouseY.toDouble())
		extractSlots(graphics)
	}

	override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, deltaTicks: Float) {
		val pose = graphics.pose()
		pose.pushMatrix()
		val backgroundWidth = gameWidth * totalSlotSpace
		val backgroundHeight = gameHeight * totalSlotSpace
		val x = (graphics.guiWidth() - backgroundWidth) / 2f - config.gap
		val y = (graphics.guiHeight() - backgroundHeight) / 2f - config.gap
		pose.translate(x, y)
		graphics.drawRoundedRect(
			-config.padding, -config.padding,
			backgroundWidth + config.padding * 2,
			backgroundHeight + config.padding * 2,
			config.backgroundRoundness,
			config.backgroundColor
		)
		pose.popMatrix()
	}

	abstract fun extractSlots(graphics: GuiGraphicsExtractor)

	protected fun extractSlot(graphics: GuiGraphicsExtractor, slotIndex: Int, color: ChromaColour, text: String = "") {
		val x = (slotIndex % 9 - 4) * totalSlotSpace + (graphics.guiWidth() - totalSlotSpace) / 2

		val rowOffset = when (gameHeight) {
			4 -> scaledSlotSize / 2 + 1f
			2 -> -(scaledSlotSize / 2) - 1f
			else -> 0f
		}
		val y = (slotIndex / 9 - 2) * totalSlotSpace + (graphics.guiHeight() - totalSlotSpace) / 2 - rowOffset

		slotMap[slotIndex] = Box(x, y, scaledSlotSize)

		val pose = graphics.pose()
		pose.pushMatrix()
		pose.translate(x, y)
		pose.scale(config.customTermSize)

		graphics.drawRoundedRect(0f, 0f, 16f, 16f, config.slotRoundness, color)
		graphics.centeredText(font, text, 8, font.lineHeight / 2, CommonColors.WHITE)
		if (config.debug) {
			graphics.fakeItem(menu.getSlot(slotIndex).item, 0, 0)
			graphics.itemDecorations(font, menu.getSlot(slotIndex).item, 0, 0)
		}

		pose.popMatrix()
	}

	override fun getMenu(): ChestMenu = menu

	override fun onClose() {
		val player = minecraft.player ?: return
		if (isSimulator) player.clientSideCloseContainer() else player.closeContainer()
		DungeonEvents.TERMINAL_CLOSED.invoker().onClose()
	}

	override fun removed() {
		val player = minecraft.player ?: return
		menu.removed(player)
		Terminal.handler?.removed()
	}

	protected fun getHoveredSlot(mouseX: Double, mouseY: Double): Int? {
		return slotMap.entries.find { isHovering(it.value.x, it.value.y, it.value.l, mouseX, mouseY) }?.key
	}

	private fun isHovering(slotX: Float, slotY: Float, length: Float, mouseX: Double, mouseY: Double): Boolean {
		return mouseX in slotX..(slotX + length) && mouseY in slotY..(slotY + length)
	}

	open fun isExpected(slotIndex: Int, itemStack: ItemStack) = true

	private fun confirmClickedSlotUpdate(slotIndex: Int, itemStack: ItemStack) {
		if (clickedSlots.isEmpty()) return

		val iterator = clickedSlots.iterator()
		while (iterator.hasNext()) {
			val slotToTime = iterator.next()
			if (slotIndex == slotToTime.first && isExpected(slotIndex, itemStack)) {
				iterator.remove()
				break
			}
		}
	}

	fun slotChanged(container: AbstractContainerMenu, slotIndex: Int, itemStack: ItemStack) {
		Terminal.handler?.isTerminalSolved(slots)
		confirmClickedSlotUpdate(slotIndex, itemStack)

		if (slotIndex == menu.container.containerSize - 1 && !isInitialized) {
			solution.addAll(solveTerminal(slots))
			isInitialized = true
			WpcMod.LOGGER.debug("Initialized terminal with solution {}", solution)
		}
	}

	abstract fun solveTerminal(slots: List<Slot>): List<Int>

	private data class Box(val x: Float, val y: Float, val l: Float)
}