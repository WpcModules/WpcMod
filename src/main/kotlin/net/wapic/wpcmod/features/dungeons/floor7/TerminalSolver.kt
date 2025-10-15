package net.wapic.wpcmod.features.dungeons.floor7

import net.wapic.wpcmod.features.dungeons.floor7.termsim.TermSimGUI
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.screen.sync.ItemStackHash
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalHandler
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalTypes
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.config.dungeon.Floor7Config.TerminalSolverConfig.RenderType
import net.wapic.wpcmod.events.TooltipEvents
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.MelodyHandler
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.NumbersHandler
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.PanesHandler
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.RubixHandler
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.SelectAllHandler
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.StartsWithHandler
import net.wapic.wpcmod.mixin.accessors.HandledScreenAccessor
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
		PacketEvents.RECEIVE.register(::onPacketReceive)
		PacketEvents.SEND.register(::onPacketSend)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		GuiEvents.DRAW_SLOT_FOREGROUND.register(::drawSlot)
		GuiEvents.MOUSE_CLICK.register(::onGuiClick)
		TooltipEvents.RENDER.register(::onTooltipDraw)
		GuiEvents.RENDER.register(::onGuiRender)
		GuiEvents.DRAW_BACKGROUND.register(::onDrawBackground)
	}

    fun onPacketReceive(packet: Packet<out PacketListener>) {
		if(!config.enabled) return

        when (packet) {
            is OpenScreenS2CPacket -> {
                currentTerm?.let { if (!it.isClicked && MC.screen !is TermSimGUI) leftTerm() }
                val windowName = packet.name?.string ?: return
                val newTermType = TerminalTypes.entries.find { terminal -> windowName.startsWith(terminal.windowName) }?.takeIf { it != currentTerm?.type } ?: return

                currentTerm = when (newTermType) {
                    TerminalTypes.PANES -> PanesHandler()

                    TerminalTypes.RUBIX -> RubixHandler()

                    TerminalTypes.NUMBERS -> NumbersHandler()

                    TerminalTypes.STARTS_WITH ->
						StartsWithHandler(
							startsWithRegex.find(windowName)?.groupValues?.get(1)
								?: return ChatUtils.sendMessage("Failed to find letter, please report this!")
						)

                    TerminalTypes.SELECT ->
						SelectAllHandler(
							selectAllRegex.find(windowName)?.groupValues?.get(1)
								?.replace("light blue", "aqua", true)
								?.replace("light gray", "silver", true)
								?.replace("_", " ")
								?: return ChatUtils.sendMessage("Failed to find color, please report this!")
						)

                    TerminalTypes.MELODY -> MelodyHandler()
                }

                currentTerm?.let {
					WpcMod.logger.debug("§aNew terminal: §6${it.type.name}")
					DungeonEvents.TERMINAL_OPENED.invoker().onOpen(it)
                    lastTermOpened = it
                }
            }

            is CloseScreenS2CPacket -> leftTerm()
        }
    }

	fun onMessageReceived(text: Text, actionBar: Boolean) {
		if(actionBar || !config.enabled) return

		termSolverRegex.find(text.string)?.let { message ->
			if (message.groupValues[1] == MC.player?.name?.string) lastTermOpened?.let { DungeonEvents.TERMINAL_SOLVED.invoker().onSolve(it) }
		}
	}

    fun onPacketSend(packet: Packet<out PacketListener>, callbackInfo: CallbackInfo) {
		if(!config.enabled) return
        when (packet) {
            is CloseHandledScreenC2SPacket -> leftTerm()

            is ClickSlotC2SPacket -> {
                lastClickTime = System.currentTimeMillis()
                currentTerm?.isClicked = true
            }

            is CommonPingS2CPacket -> {
                if (System.currentTimeMillis() - lastClickTime >= config.terminalReloadThreshold && currentTerm?.isClicked == true) currentTerm?.let {
                    PacketEvents.RECEIVE.invoker().onPacketReceive(ClickSlotC2SPacket(MC.player?.currentScreenHandler?.syncId ?: -1, 0, 0, 0, SlotActionType.PICKUP, Int2ObjectMaps.emptyMap(), ItemStackHash.EMPTY))
                    it.isClicked = false
                }
            }

            else -> return
        }
        if (packet is CloseHandledScreenC2SPacket) leftTerm()
    }

    fun onGuiClick(screen: Screen, mouseX: Int, mouseY: Int, button: Int, callbackInfoReturnable: CallbackInfoReturnable<Boolean>) = with(currentTerm) {
        if (!config.enabled || this == null) return

        if (config.renderType == RenderType.CUSTOM && !(type == TerminalTypes.MELODY && config.cancelMelodySolver)) {
            currentTerm?.type?.getGUI()?.mouseClicked(screen, button)
            callbackInfoReturnable.cancel()
            return
        }

        val slotIndex = (MC.screen as HandledScreenAccessor).focusedSlot()?.id ?: return

        if (config.blockIncorrectClicks && !canClick(slotIndex, button)) {
            callbackInfoReturnable.cancel()
            return
        }

        if (config.middleClickGUI) {
            click(slotIndex, button, config.hideClicked && !isClicked)
            callbackInfoReturnable.cancel()
            return
        }

        if (config.hideClicked && !isClicked) {
            simulateClick(slotIndex, button)
            isClicked = true
        }
    }

    fun onGuiRender(screen: Screen, drawContext: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float, callbackInfo: CallbackInfo) {
        if (!config.enabled || currentTerm == null || (currentTerm?.type == TerminalTypes.MELODY && config.cancelMelodySolver)) return

        if (config.renderType == RenderType.CUSTOM) {
            currentTerm?.type?.getGUI()?.render(drawContext)
            callbackInfo.cancel()
        }
    }

    fun onDrawBackground(screen: Screen, drawContext: DrawContext) {
        if (!config.enabled || currentTerm == null || (currentTerm?.type == TerminalTypes.MELODY && config.cancelMelodySolver) || config.renderType != RenderType.NORMAL) return
        val screen = (screen as? HandledScreen<*>) as? HandledScreenAccessor ?: return
        drawContext.fill(screen.x + 7, screen.y + 16, screen.x + screen.width - 7, screen.y + screen.height - 96, config.backgroundColor.getEffectiveColourRGB())
    }

    fun drawSlot(drawContext: DrawContext, slot: Slot, callbackInfo: CallbackInfo) = with(currentTerm) {
        if (!config.hideClicked || config.renderType == RenderType.CUSTOM || this?.type == null || (type == TerminalTypes.MELODY && config.cancelMelodySolver)) return

        val slotIndex = slot.id
        val inventorySize = (MC.screen as? HandledScreen<*>)?.screenHandler?.slots?.size ?: return

        callbackInfo.cancel()
        if (slotIndex !in solution || slotIndex > inventorySize - 37) return

        when (type) {
            TerminalTypes.PANES -> drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, config.panesColor.getEffectiveColourRGB())

            TerminalTypes.STARTS_WITH, TerminalTypes.SELECT ->
                drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, config.startsWithColor.getEffectiveColourRGB())

            TerminalTypes.NUMBERS -> {
                val index = solution.indexOf(slot.index)
                if (index < 3) {
                    val color = when (index) {
                        0 -> config.orderColor
                        1 -> config.orderColor2
                        else -> config.orderColor3
                    }.getEffectiveColourRGB()
                    drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, color)
                    callbackInfo.cancel()
                }
                val amount = slot.stack?.count?.toString() ?: ""
                if (config.showNumbers)
					drawContext.drawText(MC.textRenderer, amount, slot.x + 8 - MC.textRenderer.getWidth(amount) / 2, slot.y + 4, Colors.WHITE, false)
            }

            TerminalTypes.RUBIX -> {
                val needed = solution.count { it == slotIndex }
                val text = if (needed < 3) needed else (needed - 5)
                if (text != 0) {
                    val color = when (text) {
                        2 -> config.rubixColor2
                        1 -> config.rubixColor1
                        -2 -> config.oppositeRubixColor2
                        else -> config.oppositeRubixColor1
                    }

                    drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, color.getEffectiveColourRGB())
                    drawContext.drawText(MC.textRenderer, text.toString(), slot.x + 8 - MC.textRenderer.getWidth(text.toString()) / 2, slot.y + 4, Colors.WHITE, true)
                }
            }

            TerminalTypes.MELODY -> {
                drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, when {
                    slotIndex / 9 == 0 || slotIndex / 9 == 5 -> config.melodyColumColor
                    (slotIndex % 9).equalsOneOf(1, 2, 3, 4, 5) -> config.melodyPointerColor
                    else -> config.melodyPointerColor
                }.getEffectiveColourRGB())
            }
        }
    }

    fun onTooltipDraw(screen: Screen, mouseX: Int, mouseY: Int, drawContext: DrawContext, callbackInfo: CallbackInfo) {
        if (config.enabled && config.cancelToolTip && currentTerm != null) callbackInfo.cancel()
    }

    private fun leftTerm() {
        currentTerm?.let {
            WpcMod.logger.debug("§cLeft terminal: §6${it.type.name}")
			DungeonEvents.TERMINAL_CLOSED.invoker().onClose(it)
            currentTerm?.type?.getGUI()?.closeGui()
            currentTerm = null
        }
    }
}