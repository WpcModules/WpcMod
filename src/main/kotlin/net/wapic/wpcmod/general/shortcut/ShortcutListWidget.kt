package net.wapic.wpcmod.general.shortcut

import com.google.common.collect.ImmutableList
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.wapic.wpcmod.general.shortcut.ShortcutHandler
import net.wapic.wpcmod.general.shortcut.ShortcutListWidget.Entry
import java.util.function.Consumer

class ShortcutListWidget : ElementListWidget<Entry> {

    var parent: ShortcutScreen? = null

    constructor(parent: ShortcutScreen, client: MinecraftClient) : super(client, parent.width, parent.layout.contentHeight, parent.layout.headerHeight, 20) {
        this.parent = parent
        for (shortcut in ShortcutHandler.allShortcuts) {
            addShortcutEntry(shortcut)
        }
    }

    fun addShortcutEntry(shortcut: Shortcut){
        this.addEntry(ShortcutEntry(shortcut))
    }

    fun update() {
        Shortcut.updateKeysByCode()
        this.updateChildren()
    }

    fun updateChildren() {
        this.children().forEach(Consumer { obj: Entry -> obj.update() })
    }

    override fun getRowWidth(): Int {
        return 340
    }

    abstract class Entry : ElementListWidget.Entry<Entry>() {
        abstract fun update()
    }

    inner class ShortcutEntry internal constructor(private val binding: Shortcut) : Entry() {
        private val commandField: TextFieldWidget
        private val editButton: ButtonWidget
        private val deleteButton: ButtonWidget
        private val client: MinecraftClient = MinecraftClient.getInstance()
        private var duplicate = false

        init {
            this.commandField = TextFieldWidget(client.textRenderer, 120, 20, Text.of(binding.getCommand()))
            this.commandField.text = binding.getCommand()
            this.commandField.setChangedListener { command ->
                binding.setCommand(command)
                this.update()
            }

            this.editButton = ButtonWidget.builder(Text.of("")) { button: ButtonWidget? ->
                this@ShortcutListWidget.parent?.selectedShortcut = binding
                this.update()
            }.dimensions(0, 0, 75, 20).build()

            this.deleteButton = ButtonWidget.builder(Text.of("Delete")) { button: ButtonWidget? ->
                this@ShortcutListWidget.removeEntry(this)
                ShortcutHandler.allShortcuts.remove(binding)
                this.update()
            }.dimensions(0, 0, 50, 20).build()

            this.update()
        }

        override fun render(
            context: DrawContext,
            index: Int,
            y: Int,
            x: Int,
            entryWidth: Int,
            entryHeight: Int,
            mouseX: Int,
            mouseY: Int,
            hovered: Boolean,
            tickProgress: Float
        ) {
            val i: Int = this@ShortcutListWidget.scrollbarX - this.deleteButton.width - 10
            val j = y - 2
            this.deleteButton.setPosition(i, j)
            this.deleteButton.render(context, mouseX, mouseY, tickProgress)
            val k = i - 5 - this.editButton.width
            this.editButton.setPosition(k, j)
            this.editButton.render(context, mouseX, mouseY, tickProgress)
            val l = k - 90 - this.commandField.width
            this.commandField.setPosition(l, j)
            this.commandField.render(context, mouseX, mouseY, tickProgress)

            if (this.duplicate) {
                val l = 3
                val m = this.editButton.x - 6
                context.fill(m, y - 1, m + 3, y + entryHeight, -65536)
            }
        }

        override fun children(): MutableList<out Element?> {
            return ImmutableList.of(this.commandField, this.editButton, this.deleteButton)
        }

        override fun selectableChildren(): MutableList<out Selectable?> {
            return ImmutableList.of(this.commandField, this.editButton, this.deleteButton)
        }

        override fun update() {
            this.editButton.message = this.binding.getBoundKeyText()
            this.duplicate = false
            val mutableText = Text.empty()

            if (!this.binding.isUnbound()) {

                for (shortcut in ShortcutHandler.allShortcuts) {
                    if (shortcut !== this.binding && this.binding.equals(shortcut)) {
                        if (this.duplicate) {
                            mutableText.append(", ")
                        }

                        this.duplicate = true
                        mutableText.append(shortcut.getCommand())
                    }
                }

            }

            if (this.duplicate) {
                this.editButton.message = Text.literal("[ ").append(
                    this.editButton.message.copy().formatted(
                        Formatting.WHITE
                    )
                ).append(" ]").formatted(Formatting.RED)
            }

            if (this@ShortcutListWidget.parent?.selectedShortcut === this.binding) {
                this.editButton.message = Text.literal("> ").append(
                    this.editButton.message.copy().formatted(
                        *arrayOf(
                            Formatting.WHITE, Formatting.UNDERLINE
                        )
                    )
                ).append(" <").formatted(Formatting.YELLOW)
            }
        }
    }
}