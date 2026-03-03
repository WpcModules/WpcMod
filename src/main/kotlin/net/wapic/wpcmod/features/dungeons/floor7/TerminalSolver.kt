package net.wapic.wpcmod.features.dungeons.floor7

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.HashedStack
import net.minecraft.network.PacketListener
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.ClientboundPingPacket
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket
import net.minecraft.util.CommonColors
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.DyeColor
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.compat.ReiCompatibility
import net.wapic.wpcmod.config.dungeon.Floor7Config.TerminalSolverConfig.RenderType
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.events.PacketEvents
import net.wapic.wpcmod.events.TooltipEvents
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.*
import net.wapic.wpcmod.features.dungeons.floor7.termsim.TermSimGUI
import net.wapic.wpcmod.mixin.accessors.AbstractContainerScreenAccessor
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.MC
import org.lwjgl.glfw.GLFW
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
	private var wasREIVisible = false

	fun init() {
		PacketEvents.RECEIVE.register(::onPacketReceive)
		PacketEvents.SEND_BEFORE.register(::onPacketSend)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		GuiEvents.DRAW_SLOT_BACKGROUND.register(::drawSlot)
		GuiEvents.MOUSE_CLICK.register(::onGuiClick)
		GuiEvents.SLOT_CLICKED.register(::onSlotClick)
		TooltipEvents.RENDER.register(::onTooltipDraw)
		GuiEvents.RENDER.register(::onGuiRender)
		GuiEvents.DRAW_BACKGROUND.register(::onDrawBackground)
		DungeonEvents.TERMINAL_OPENED.register {
			ReiCompatibility.setOverlayVisible(false)
			wasREIVisible = true
		}
		DungeonEvents.TERMINAL_CLOSED.register {
			if (!wasREIVisible) return@register
			ReiCompatibility.setOverlayVisible(true)
			wasREIVisible = false
		}
	}

    fun onPacketReceive(packet: Packet<out PacketListener>) {
		if(!config.enabled) return

        when (packet) {
            is ClientboundOpenScreenPacket -> {
                currentTerm?.let { if (!it.isClicked && MC.screen !is TermSimGUI) leftTerm() }
                val windowName = packet.title?.string ?: return
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
							DyeColor.entries.find {
								it.name.replace("_", " ").equals(
									selectAllRegex.find(windowName)?.groupValues?.get(1)
										?.replace("SILVER", "LIGHT GRAY"), true
								)
							} ?: return ChatUtils.sendMessage("Failed to find color, please report this!")
						)

                    TerminalTypes.MELODY -> MelodyHandler()
                }

                currentTerm?.let {
					WpcMod.logger.debug("§aNew terminal: §6${it.type.name}")
					DungeonEvents.TERMINAL_OPENED.invoker().onOpen(it)
					it.containerId = packet.containerId
                    lastTermOpened = it
                }
            }

            is ClientboundContainerClosePacket -> leftTerm()
        }
    }

	fun onMessageReceived(text: Component, actionBar: Boolean) {
		if(actionBar || !config.enabled) return

		termSolverRegex.find(text.string)?.let { message ->
			if (message.groupValues[1] == MC.player?.name?.string) lastTermOpened?.let { DungeonEvents.TERMINAL_SOLVED.invoker().onSolve(it) }
		}
	}

    fun onPacketSend(packet: Packet<out PacketListener>, callbackInfo: CallbackInfo) {
		if(!config.enabled) return
        when (packet) {
            is ServerboundContainerClosePacket -> leftTerm()

            is ServerboundContainerClickPacket -> {
                lastClickTime = System.currentTimeMillis()
                currentTerm?.isClicked = true
            }

            is ClientboundPingPacket -> {
                if (System.currentTimeMillis() - lastClickTime >= config.terminalReloadThreshold && currentTerm?.isClicked == true) currentTerm?.let {
                    PacketEvents.RECEIVE.invoker().onPacketReceive(
						ServerboundContainerClickPacket(MC.player?.containerMenu?.containerId ?: -1, 0, 0, 0, ClickType.PICKUP, Int2ObjectMaps.emptyMap(), HashedStack.EMPTY))
                    it.isClicked = false
                }
            }

            else -> return
        }
    }

    fun onGuiClick(screen: Screen, mouseX: Int, mouseY: Int, button: Int, callbackInfoReturnable: CallbackInfoReturnable<Boolean>) = with(currentTerm) {
        if (!config.enabled || this == null) return

        if (config.renderType == RenderType.CUSTOM && !(type == TerminalTypes.MELODY && config.cancelMelodySolver)) {
            currentTerm?.type?.getGUI()?.mouseClicked(screen, button)
            callbackInfoReturnable.cancel()
            return
        }
    }

	fun onSlotClick(slot: Slot?, slotId: Int, button: Int, slotActionType: ClickType, callbackInfo: CallbackInfo) =
		with(currentTerm) {
			if (!config.enabled || this == null) return

			if (config.renderType == RenderType.CUSTOM && !(type == TerminalTypes.MELODY && config.cancelMelodySolver) || (config.blockIncorrectClicks && !canClick(
					slotId,
					button
				))
			) {
				return callbackInfo.cancel()
			}

			if (config.middleClickGUI) {
				click(
					slotId,
					if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) GLFW.GLFW_MOUSE_BUTTON_3 else button,
					config.hideClicked && !isClicked
				)
				return callbackInfo.cancel()
			}

			if (config.hideClicked && !isClicked) {
				simulateClick(slotId, button)
				isClicked = true
			}
		}

	fun onDrawBackground(screen: Screen, drawContext: GuiGraphics, callbackInfo: CallbackInfo) {
		if (!config.enabled || currentTerm == null || (currentTerm?.type == TerminalTypes.MELODY && config.cancelMelodySolver) || config.renderType != RenderType.CUSTOM) return
		currentTerm?.type?.getGUI()?.render(drawContext)
		callbackInfo.cancel()
	}

	fun onGuiRender(
		screen: Screen,
		drawContext: GuiGraphics,
		mouseX: Int,
		mouseY: Int,
		deltaTicks: Float,
		callbackInfo: CallbackInfo
	) {
		if (!config.enabled || currentTerm == null || (currentTerm?.type == TerminalTypes.MELODY && config.cancelMelodySolver)) return
		if (config.renderType == RenderType.CUSTOM) {
			callbackInfo.cancel()
			return
		}

		val screen = (screen as? AbstractContainerScreen<*>) as? AbstractContainerScreenAccessor ?: return
		drawContext.fill(
			screen.leftPos + 7,
			screen.topPos + 16,
			screen.leftPos + screen.width - 7,
			screen.topPos + screen.height - 96,
			config.backgroundColor.getEffectiveColourRGB()
		)
	}

	fun drawSlot(drawContext: GuiGraphics, screen: Screen, slot: Slot, callbackInfo: CallbackInfo) = with(currentTerm) {
		if (!config.enabled || config.renderType == RenderType.CUSTOM || this?.type == null || type == TerminalTypes.MELODY) return

        val slotIndex = slot.index
		val inventorySize = (screen as? AbstractContainerScreen<*>)?.menu?.slots?.size ?: return

        callbackInfo.cancel()
        if (slotIndex !in solution || slotIndex > inventorySize - 37) return

        when (type) {
            TerminalTypes.PANES -> drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, config.panesColor.getEffectiveColourRGB())
			TerminalTypes.SELECT -> drawContext.fill(
				slot.x,
				slot.y,
				slot.x + 16,
				slot.y + 16,
				config.selectColor.getEffectiveColourRGB()
			)
			TerminalTypes.STARTS_WITH -> drawContext.fill(
				slot.x,
				slot.y,
				slot.x + 16,
				slot.y + 16,
				config.startsWithColor.getEffectiveColourRGB()
			)

            TerminalTypes.NUMBERS -> {
                val index = solution.indexOf(slot.containerSlot)
                if (index < 3) {
                    val color = when (index) {
                        0 -> config.orderColor
                        1 -> config.orderColor2
                        else -> config.orderColor3
                    }.getEffectiveColourRGB()
                    drawContext.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, color)
                    callbackInfo.cancel()
                }
                val amount = slot.item?.count?.toString() ?: ""
                if (config.showNumbers)
					drawContext.drawString(
						MC.font,
						amount,
						slot.x + 8 - MC.font.width(amount) / 2,
						slot.y + 4,
						CommonColors.WHITE,
						true
					)
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
					drawContext.drawString(
						MC.font,
						text.toString(),
						slot.x + 8 - MC.font.width(text.toString()) / 2,
						slot.y + 4,
						CommonColors.WHITE,
						true
					)
                }
            }

			else -> return@with
		}
    }

    fun onTooltipDraw(screen: Screen, mouseX: Int, mouseY: Int, drawContext: GuiGraphics, callbackInfo: CallbackInfo) {
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