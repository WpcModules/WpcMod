package net.wapic.wpcmod.features.general.shortcut

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.DirectionalLayoutWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.Util
import org.lwjgl.glfw.GLFW

class ShortcutScreen : GameOptionsScreen(null, MinecraftClient.getInstance().options, Text.of("Command Shortcuts")) {

    var selectedShortcut: Shortcut? = null
    private var shortcutsList: ShortcutListWidget? = null
    var lastKeyCodeUpdateTime: Long = 0

    override fun initBody() {
        this.shortcutsList = this.layout.addBody(ShortcutListWidget(this, this.client!!))
    }

    override fun addOptions() {

    }

    override fun close() {
        ShortcutHandler.saveShortcuts()
        super.close()
    }

    override fun refreshWidgetPositions() {
        this.layout.refreshPositions()
        this.shortcutsList?.position(this.width, this.layout)
    }

    override fun initFooter() {
        val newShortcut: ButtonWidget = ButtonWidget.builder(Text.of("New shortcut")) {
            val shortcut = Shortcut("", GLFW.GLFW_KEY_UNKNOWN, GLFW.GLFW_KEY_UNKNOWN)
            this.shortcutsList?.addShortcutEntry(shortcut)
            ShortcutHandler.loadedShortcuts.add(shortcut)
        }.build()
        val close: ButtonWidget = ButtonWidget.builder(ScreenTexts.DONE) { close() }.build()

        val directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8))
        directionalLayoutWidget.add(newShortcut)
        directionalLayoutWidget.add(close)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if(this.selectedShortcut != null) {
            if (keyCode == 256) {
                this.selectedShortcut?.setBoundKey(GLFW.GLFW_KEY_UNKNOWN, GLFW.GLFW_KEY_UNKNOWN)
            } else {
                this.selectedShortcut?.setBoundKey(keyCode, scanCode)
            }

            this.selectedShortcut = null
            this.lastKeyCodeUpdateTime = Util.getMeasuringTimeMs()
            this.shortcutsList?.update()
            return true
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers)
        }
    }
}