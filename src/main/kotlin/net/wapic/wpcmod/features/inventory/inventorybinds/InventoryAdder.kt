package net.wapic.wpcmod.features.inventory.inventorybinds

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text

class InventoryAdder(val parent: Screen) : Screen(Text.of("Add new Inventory")) {

	override fun init() {
		var inventoryTitle = ""
		val textWidget = TextFieldWidget(
			client?.textRenderer,
			this.width / 2 - 100,
			this.height / 2 - 30,
			200,
			20,
			Text.of("Inventory Title Here...")
		)
		textWidget.setChangedListener { inventoryTitle = it }

		this.addDrawableChild(textWidget)
		this.addDrawableChild(
			ButtonWidget.builder(Text.of("Save & Close")) {
				if (inventoryTitle.isNotEmpty()) {
					InventoryBindsHandler.loadedInventoryBinds.putIfAbsent(inventoryTitle, mutableListOf())
				}
				close()
			}.dimensions(this.width / 2, this.height / 2, 100, 20).build()
		)

		this.addDrawableChild(
			ButtonWidget.builder(Text.of("Close")) { close() }
				.dimensions(this.width / 2 - 100, this.height / 2, 100, 20).build()
		)
		super.init()
	}

	override fun close() {
		this.client?.setScreen(parent)
	}
}