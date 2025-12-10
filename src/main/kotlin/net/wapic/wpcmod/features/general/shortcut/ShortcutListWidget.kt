package net.wapic.wpcmod.features.general.shortcut

import com.google.common.collect.ImmutableList
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.ContainerObjectSelectionList
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.wapic.wpcmod.features.general.shortcut.ShortcutListWidget.Entry
import net.wapic.wpcmod.util.MC
import java.util.function.Consumer

class ShortcutListWidget : ContainerObjectSelectionList<Entry> {

	var parent: ShortcutScreen? = null

	constructor(parent: ShortcutScreen, client: Minecraft) : super(
		client, parent.width, parent.layout.contentHeight, parent.layout.headerHeight, 20
	) {
		this.parent = parent

		ShortcutHandler.loadedShortcuts.forEach {
			addShortcutEntry(it)
		}
	}

	fun addShortcutEntry(shortcut: Shortcut) {
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

	abstract class Entry : ContainerObjectSelectionList.Entry<Entry>() {

		abstract fun update()
	}

	inner class ShortcutEntry internal constructor(private val binding: Shortcut) : Entry() {

		private val commandField: EditBox = EditBox(MC.textRenderer, 120, 20, Component.nullToEmpty(binding.getCommand()))
		private val editButton: Button
		private val deleteButton: Button
		private var duplicate = false

		init {
			this.commandField.value = binding.getCommand()
			this.commandField.setResponder { command ->
				binding.setCommand(command)
				this@ShortcutListWidget.update()
			}

			this.editButton = Button.builder(Component.nullToEmpty("")) { button: Button? ->
				this@ShortcutListWidget.parent?.selectedShortcut = binding
				this@ShortcutListWidget.update()
			}.bounds(0, 0, 75, 20).build()

			this.deleteButton = Button.builder(Component.nullToEmpty("Delete")) { button: Button? ->
				this@ShortcutListWidget.removeEntry(this)
				this@ShortcutListWidget.update()
				ShortcutHandler.loadedShortcuts.removeIf { it == binding }
			}.bounds(0, 0, 50, 20).build()

			this.update()
		}

		override fun renderContent(context: GuiGraphics, mouseX: Int, mouseY: Int, hovered: Boolean, tickProgress: Float) {
			val i: Int = this@ShortcutListWidget.scrollBarX() - this.deleteButton.width - 10
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
				val m = this.editButton.x - 6
				context.fill(m, y - 1, m + 3, y + contentHeight, -65536)
			}
		}

		override fun children(): MutableList<out GuiEventListener?> {
			return ImmutableList.of(this.commandField, this.editButton, this.deleteButton)
		}

		override fun narratables(): MutableList<out NarratableEntry?> {
			return ImmutableList.of(this.commandField, this.editButton, this.deleteButton)
		}

		override fun update() {
			this.editButton.message = this.binding.getBoundKeyText()
			this.duplicate = false
			val mutableText = Component.empty()

			if (!this.binding.isUnbound()) {

				for (shortcut in ShortcutHandler.loadedShortcuts) {
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
				this.editButton.message = Component.literal("[ ").append(
					this.editButton.message.copy().withStyle(
						ChatFormatting.WHITE
					)
				).append(" ]").withStyle(ChatFormatting.RED)
			}

			if (this@ShortcutListWidget.parent?.selectedShortcut === this.binding) {
				this.editButton.message = Component.literal("> ").append(
					this.editButton.message.copy().withStyle(
						*arrayOf(
							ChatFormatting.WHITE, ChatFormatting.UNDERLINE
						)
					)
				).append(" <").withStyle(ChatFormatting.YELLOW)
			}
		}
	}
}