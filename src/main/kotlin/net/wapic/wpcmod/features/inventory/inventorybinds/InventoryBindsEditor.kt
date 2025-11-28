package net.wapic.wpcmod.features.inventory.inventorybinds

import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.input.KeyInput
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.Util

class InventoryBindsEditor : Screen(Text.of("Inventory Binds")) {

	var selectedInventoryBind: InventoryBind? = null
	var lastKeyCodeUpdateTime: Long = 0

	override fun init() {
		super.init()
	}

	override fun close() {
		InventoryBindsHandler.saveInventoryBinds()
		super.close()
	}

	override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, deltaTicks: Float) {
		super.render(context, mouseX, mouseY, deltaTicks)
	}

	override fun mouseClicked(click: Click, doubled: Boolean): Boolean {
		if (this.selectedInventoryBind == null) return super.mouseClicked(click, doubled)
		this.selectedInventoryBind?.setBoundKey(InputUtil.Type.MOUSE.createFromCode(click.button()))
		this.selectedInventoryBind = null
		return true
	}

	override fun keyPressed(input: KeyInput): Boolean {
		if (this.selectedInventoryBind == null) return super.keyPressed(input)
		this.selectedInventoryBind?.setBoundKey(if(input.keycode == 256) InputUtil.UNKNOWN_KEY else InputUtil.fromKeyCode(input))
		this.selectedInventoryBind = null
		this.lastKeyCodeUpdateTime = Util.getMeasuringTimeMs()
		return true
	}
}