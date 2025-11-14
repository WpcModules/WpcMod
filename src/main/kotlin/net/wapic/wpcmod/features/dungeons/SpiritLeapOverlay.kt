package net.wapic.wpcmod.features.dungeons

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.input.KeyInput
import net.minecraft.client.util.InputUtil
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.Colors
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.events.InventoryEvents
import net.wapic.wpcmod.mixin.accessors.HandledScreenAccessor
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.drawText
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object SpiritLeapOverlay {

	private val config get() = WpcMod.config.dungeon.spiritLeapOverlay
	private val BG_COLOR = ChromaColour.fromStaticRGB(0, 0, 0, 90).getEffectiveColourRGB()
	private val validInventories = listOf("Teleport To Player", "Spirit Leap")
	private val leapTargets = mutableMapOf<Int, LeapTarget>()

	private const val SLOT_SIZE = 32
	private const val SLOT_GAP = 4

	private var inValidInventory = false
	private var screenHandler: ScreenHandler? = null

	private data class LeapTarget(val slotId: Int, val itemStack: ItemStack, val name: String)

	fun init() {
		InventoryEvents.OPEN.register(::onGuiOpen)
		InventoryEvents.SLOT_UPDATE.register(::onSlotUpdate)
		GuiEvents.DRAW_BACKGROUND.register(::onDrawBackground)
		GuiEvents.RENDER.register(::onGuiRender)
		GuiEvents.MOUSE_CLICK.register(::onMouseClick)
		GuiEvents.KEY_PRESSED.register(::onKeyPressed)

		InventoryEvents.CLOSE.register {
			inValidInventory = false
			screenHandler = null
			leapTargets.clear()
		}
	}

	private fun onGuiOpen(title: String) {
		if(!config.enabled) return
		inValidInventory = title in validInventories
		screenHandler = MC.player?.currentScreenHandler ?: return
	}

	private fun onSlotUpdate(syncId: Int, slotId: Int, itemStack: ItemStack) {
		if(!inValidInventory || itemStack.isEmpty) return
		val bind = screenHandler?.slots?.size?.minus(slotId) ?: return
		leapTargets[bind] = LeapTarget(slotId, itemStack, itemStack.name.string)
	}

	private fun onDrawBackground(screen: Screen, context: DrawContext, ci: CallbackInfo) {
		if(!inValidInventory) return
		val handledScreen = screen as? HandledScreenAccessor ?: return
		val matrixStack = context.matrices
		val width = leapTargets.entries.size * (SLOT_SIZE + SLOT_GAP) / 2

		matrixStack.pushMatrix()
		matrixStack.translate(handledScreen.width / 2f, handledScreen.height - SLOT_SIZE / 2f)
		context.fill(-width, -SLOT_SIZE / 2, width * 2, SLOT_SIZE / 2, BG_COLOR)

		matrixStack.popMatrix()
		ci.cancel()
	}

	// 9x3 Generic Container Screen?
	private fun onGuiRender(screen: Screen, context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float, ci: CallbackInfo) {
		if(!inValidInventory) return
		val handledScreen = screen as? HandledScreenAccessor ?: return
		val matrixStack = context.matrices

		matrixStack.pushMatrix()
		matrixStack.translate(handledScreen.width / 2f, handledScreen.height + SLOT_SIZE / 2f)

		for (player in leapTargets.values) {
			matrixStack.pushMatrix()
			matrixStack.translate((player.slotId % 9 - 3f) * (SLOT_SIZE + SLOT_GAP), 0f) // This is assuming players are on slot 10, 11, 12, 13

			context.fill(0, 0, SLOT_SIZE, SLOT_SIZE, BG_COLOR)
			context.drawItem(player.itemStack, 0, 0)
			context.drawText(player.name, 0, SLOT_SIZE - 11, Colors.WHITE, true)

			matrixStack.popMatrix()
		}

		matrixStack.popMatrix()
		ci.cancel()
	}

	private fun onKeyPressed(input: KeyInput, ci: CallbackInfo) {
		if(!inValidInventory || config.keybinds) return

		screenHandler?.let {
			val leapTarget = leapTargets[input.key] ?: return
			MC.interactionManager?.clickSlot(it.syncId, leapTarget.slotId, InputUtil.GLFW_MOUSE_BUTTON_MIDDLE, SlotActionType.CLONE, MC.player)
			if(config.announce) Utils.runCommand("pc Leaped to: ${leapTarget.name}")
		}

		ci.cancel()
	}

	private fun onMouseClick(screen: Screen, mouseX: Int, mouseY: Int, button: Int, ci: CallbackInfo) {
		if(!inValidInventory) return
		ci.cancel()
	}
}