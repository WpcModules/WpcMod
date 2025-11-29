package net.wapic.wpcmod.features.inventory.inventorybinds

import net.minecraft.client.gui.Click
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.DirectionalLayoutWidget
import net.minecraft.client.input.KeyInput
import net.minecraft.client.util.InputUtil
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.Util
import net.wapic.wpcmod.util.MC

class InventoryEditor(parent: InventoryBindsEditor, val inventory: String) :
	GameOptionsScreen(InventoryBindsEditor(), MC.options, Text.of(inventory)) {

	var selectedBind: InventoryBind? = null
	private var bindsList: InventoryBindWidget? = null
	var lastKeyCodeUpdateTime: Long = 0

	override fun initBody() {
		this.bindsList = this.layout.addBody(InventoryBindWidget(this, this.client!!))
		super.initBody()
	}

	override fun initFooter() {
		val newShortcut: ButtonWidget = ButtonWidget.builder(Text.of("New shortcut")) {
			val bind = InventoryBind(0, InputUtil.UNKNOWN_KEY)
			this.bindsList?.addInventoryBind(bind)
			InventoryBindsHandler.loadedInventoryBinds[inventory]?.add(bind)
		}.build()
		val close: ButtonWidget = ButtonWidget.builder(ScreenTexts.DONE) { close() }.build()

		val directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8))
		directionalLayoutWidget.add(newShortcut)
		directionalLayoutWidget.add(close)
	}

	override fun refreshWidgetPositions() {
		this.layout.refreshPositions()
		this.bindsList?.position(this.width, this.layout)
	}

	override fun addOptions() {
	}

	override fun close() {
		InventoryBindsHandler.saveInventoryBinds()
		client?.setScreen(parent)
	}

	override fun mouseClicked(click: Click, doubled: Boolean): Boolean {
		if (this.selectedBind != null) {
			this.selectedBind?.setBoundKey(InputUtil.Type.MOUSE.createFromCode(click.button()))
			this.selectedBind = null
			this.bindsList?.update()
			return true
		} else {
			return super.mouseClicked(click, doubled)
		}

	}

	override fun keyPressed(input: KeyInput): Boolean {
		if (this.selectedBind != null) {
			if (input.keycode == 256) {
				this.selectedBind?.setBoundKey(InputUtil.UNKNOWN_KEY)
			} else {
				this.selectedBind?.setBoundKey(InputUtil.fromKeyCode(input))
			}

			this.selectedBind = null
			this.lastKeyCodeUpdateTime = Util.getMeasuringTimeMs()
			this.bindsList?.update()
			return true
		} else {
			return super.keyPressed(input)
		}
	}
}