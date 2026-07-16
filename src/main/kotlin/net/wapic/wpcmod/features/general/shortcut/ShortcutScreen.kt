package net.wapic.wpcmod.features.general.shortcut

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.util.Util
import net.wapic.wpcmod.util.MC

class ShortcutScreen(val parent: Screen?) : Screen(MC.instance, MC.font, Component.nullToEmpty("Command Shortcuts")) {

	var selectedShortcut: Shortcut? = null
	private var shortcutsList: ShortcutListWidget? = null
	val layout: HeaderAndFooterLayout = HeaderAndFooterLayout(this)
	var lastKeyCodeUpdateTime: Long = 0

	override fun init() {
		this.addContents()
		this.addFooter()
		this.layout.visitWidgets { this.addRenderableWidget(it) }
		this.repositionElements()
	}

	private fun addContents() {
		this.shortcutsList = this.layout.addToContents(ShortcutListWidget(this, this.minecraft))
	}

	override fun onClose() {
		ShortcutHandler.saveShortcuts()
		this.minecraft.gui.setScreen(this.parent)
	}

	override fun repositionElements() {
		this.layout.arrangeElements()
		this.shortcutsList?.updateSize(this.width, this.layout)
	}

	private fun addFooter() {
		val newShortcut: Button = Button.builder(Component.nullToEmpty("New shortcut")) {
			val shortcut = Shortcut("", InputConstants.UNKNOWN)
			this.shortcutsList?.addShortcutEntry(shortcut)
			ShortcutHandler.loadedShortcuts.add(shortcut)
		}.build()
		val close: Button = Button.builder(CommonComponents.GUI_DONE) { onClose() }.build()

		val directionalLayoutWidget = this.layout.addToFooter(LinearLayout.horizontal().spacing(8))
		directionalLayoutWidget.addChild(newShortcut)
		directionalLayoutWidget.addChild(close)
	}

	override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
		if (this.selectedShortcut != null) {
			this.selectedShortcut?.setBoundKey(InputConstants.Type.MOUSE.getOrCreate(click.button()))
			this.selectedShortcut = null
			this.shortcutsList?.update()
			return true
		} else {
			return super.mouseClicked(click, doubled)
		}

	}

	override fun keyPressed(input: KeyEvent): Boolean {
		if (this.selectedShortcut != null) {
			if (input.input() == 256) {
				this.selectedShortcut?.setBoundKey(InputConstants.UNKNOWN)
			} else {
				this.selectedShortcut?.setBoundKey(InputConstants.getKey(input))
			}

			this.selectedShortcut = null
			this.lastKeyCodeUpdateTime = Util.getMillis()
			this.shortcutsList?.update()
			return true
		} else {
			return super.keyPressed(input)
		}
	}
}