package net.wapic.wpcmod.features.inventory.inventorybinds

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text

class InventoryBindsEditor : Screen(Text.of("Inventory Binds")) {

	var selectedInventoryBind: InventoryBind? = null
	var lastKeyCodeUpdateTime: Long = 0

	override fun init() {
		InventoryBindsHandler.loadedInventoryBinds.keys.forEachIndexed { index, inventoryTitle ->
			val widget = ButtonWidget.Builder(Text.of(inventoryTitle)) {
				client?.setScreen(InventoryEditor(this, inventoryTitle))
			}.dimensions(this.width / 2 - 80, 10 + 20 * index, 120, 20).build()
			this.addDrawableChild(widget)

			this.addDrawableChild(ButtonWidget.Builder(Text.of("Delete")) {
				InventoryBindsHandler.loadedInventoryBinds.remove(inventoryTitle)
				this.clearAndInit()
			}.dimensions(this.width / 2 + 40, 10 + 20 * index, 40, 20).build())
		}

		this.addDrawableChild(ButtonWidget.Builder(Text.of("Add Inventory")) {
			client?.setScreen(InventoryAdder(this))
		}.dimensions(this.width / 2 - 60, this.height - 30, 120, 20).build())
		super.init()
	}

	override fun close() {
		InventoryBindsHandler.saveInventoryBinds()
		super.close()
	}

	override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, deltaTicks: Float) {
		super.render(context, mouseX, mouseY, deltaTicks)
	}
}