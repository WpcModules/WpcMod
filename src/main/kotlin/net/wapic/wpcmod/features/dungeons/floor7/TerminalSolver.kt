package net.wapic.wpcmod.features.dungeons.floor7

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.PacketListener
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import net.minecraft.util.Util
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.*
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.*
import net.wapic.wpcmod.mixin.accessors.AbstractContainerScreenAccessor
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.MC
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object TerminalSolver {
	private val config get() = WpcMod.config.dungeon.floor7.terminalSolvers

    var currentTerm: TerminalHandler? = null
        private set
    var lastTermOpened: TerminalHandler? = null
        private set
    private val termSolverRegex = Regex("^(.{1,16}) activated a terminal! \\((\\d)/(\\d)\\)$")
    private val startsWithRegex = Regex("What starts with: '(\\w+)'?")
    private val selectAllRegex = Regex("Select all the (.+) items!")
    private var lastClickTime = 0L

	fun init() {
		GuiEvents.OPEN.register(::onGuiOpen)
		GuiEvents.CLOSE.register(::onGuiClose)
		GuiEvents.RENDER.register(::onGuiRender)
		GuiEvents.DRAW_BACKGROUND.register(::onDrawBackground)
		GuiEvents.MOUSE_CLICK.register(::onMouseClick)
		GuiEvents.SLOT_UPDATE_AFTER.register(::onSlotUpdate)
		TooltipEvents.RENDER.register(::onTooltipDraw)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		ServerTickEvent.EVENT.register(::onServerTick)
		PacketEvents.SEND_BEFORE.register(::onPacketSend)
		WorldChangeEvent.BEFORE.register {
			currentTerm = null
			lastTermOpened = null
		}
	}

	fun onServerTick() {
		if (!config.enabled) return

		if (Util.getMillis() - lastClickTime >= config.terminalReloadThreshold && currentTerm?.isClicked == true) {
			currentTerm?.let {
				it.isClicked = false
			}
		}
	}

	fun onGuiOpen(screen: Screen) {
		if (screen !is ContainerScreen) return
		if (!config.enabled) return

		val title = screen.title.string

		currentTerm?.let {
			it.isClicked = false
			it.items.fill(null)
		}

		val newTerm =
			TerminalTypes.entries.find { title.startsWith(it.windowName) }?.takeIf { it != currentTerm?.type } ?: return

		currentTerm = when (newTerm) {
			TerminalTypes.MELODY -> MelodyHandler()
			TerminalTypes.NUMBERS -> NumbersHandler()
			TerminalTypes.PANES -> PanesHandler()
			TerminalTypes.RUBIX -> RubixHandler()

			TerminalTypes.SELECT_ALL -> {
				val color = selectAllRegex.find(title)?.groupValues?.get(1)?.replace("SILVER", "LIGHT GRAY")
					?: return ChatUtils.sendMessage("Failed to find color from $title")
				val dyeColor = DyeColor.entries.find { it.name.replace("_", " ") == color }
					?: return ChatUtils.sendMessage("Failed to find dyeColor from $color")
				SelectAllHandler(dyeColor)
			}

			TerminalTypes.STARTS_WITH -> {
				val letter = startsWithRegex.find(screen.title.string)?.groupValues?.get(1)
					?: return ChatUtils.sendMessage("Failed to find letter from $title, please report this!")
				StartsWithHandler(letter)
			}
		}.also {
			WpcMod.LOGGER.debug("Opened terminal: {}", it.type.name)
			DungeonEvents.TERMINAL_OPENED.invoker().onOpen(it)
			lastTermOpened = it
		}
	}

	fun onGuiClose() {
		currentTerm?.let {
			WpcMod.LOGGER.debug("Left terminal: {}", it.type.name)
			DungeonEvents.TERMINAL_CLOSED.invoker().onClose(it)
			currentTerm = null
		}
	}

	fun onGuiRender(
		screen: Screen,
		gui: GuiGraphicsExtractor,
		mouseX: Int,
		mouseY: Int,
		deltaTicks: Float,
		callbackInfo: CallbackInfo
	) {
		if (!config.enabled) return
		currentTerm?.let {
			callbackInfo.cancel()

			if (config.debug) {
				val pose = gui.pose()
				pose.pushMatrix()
				pose.translate(screen.width / 2f - 88f, -12f)
				val screen = screen as? AbstractContainerScreen<*> ?: return
				for (slot in screen.menu.slots) {
					if (slot.isActive && slot.index < 45 && slot.item.item != Items.STAINED_GLASS_PANE.black) {
						(screen as? AbstractContainerScreenAccessor)?.extractSlot_WpcMod(gui, slot, mouseX, mouseY)
						gui.text(MC.font, "${slot.index}", slot.x, slot.y, 0xFFFFFF, false)
					}
				}
				pose.popMatrix()
			}
		}
	}

	fun onDrawBackground(screen: Screen, gui: GuiGraphicsExtractor, callbackInfo: CallbackInfo) {
		if (!config.enabled) return
		currentTerm?.let {
			it.type.getGUI().render(gui, it)
			callbackInfo.cancel()
		}
	}

	fun onMouseClick(
		screen: Screen,
		mouseX: Int,
		mouseY: Int,
		button: Int,
		callbackInfoReturnable: CallbackInfoReturnable<Boolean>
	) {
		if (!config.enabled) return
		currentTerm?.let {
			it.type.getGUI().mouseClicked(screen, button, it)
			callbackInfoReturnable.cancel()
		}
	}

	fun onSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack) {
		if (!config.enabled) return
		currentTerm?.let {
			if (slotId !in 0 until it.type.windowSize) return
			it.items[slotId] = itemStack
			if (it.handleSlotUpdate(syncId, slotId, itemStack)) {
				WpcMod.LOGGER.debug("Updated terminal: {}", it.type.name)
				DungeonEvents.TERMINAL_UPDATED.invoker().onUpdate(it)
			}
		}
	}

	fun onTooltipDraw(
		screen: Screen,
		mouseX: Int,
		mouseY: Int,
		drawContext: GuiGraphicsExtractor,
		callbackInfo: CallbackInfo
	) {
		if (!config.enabled || currentTerm == null) return
		callbackInfo.cancel()
	}

	fun onMessageReceived(text: Component, actionBar: Boolean) {
		if (!config.enabled || actionBar || DungeonUtils.getF7Phase() != DungeonUtils.F7Phase.GOLDOR) return

		termSolverRegex.find(text.string)?.groupValues?.get(1)?.let { name ->
			if (name != MC.player?.name?.string) return

			lastTermOpened?.let {
				WpcMod.LOGGER.debug("Solved terminal {}", it.type)
				DungeonEvents.TERMINAL_SOLVED.invoker().onSolve(it)
			}
		}
	}

	fun onPacketSend(packet: Packet<out PacketListener>, callbackInfo: CallbackInfo) {
		if (!config.enabled || packet !is ServerboundContainerClickPacket) return

		currentTerm?.let {
			lastClickTime = System.currentTimeMillis()
			it.isClicked = true
		}
    }
}