package net.wapic.wpcmod.features.inventory.inventorybinds

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
import java.util.function.Consumer

class InventoryBindWidget : ElementListWidget<InventoryBindWidget.Entry> {

	var parent: InventoryEditor? = null

	constructor(parent: InventoryEditor, client: MinecraftClient) : super(
		client, parent.width, parent.layout.contentHeight, parent.layout.headerHeight, 20
	) {
		this.parent = parent

		InventoryBindsHandler.loadedInventoryBinds[parent.inventory]?.forEach {
			addInventoryBind(it)
		}
	}

	fun addInventoryBind(bind: InventoryBind) {
		this.addEntry(InventoryBindWidget(bind))
	}

	fun update() {
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

	inner class InventoryBindWidget internal constructor(private val bind: InventoryBind) : Entry() {

		private val slot: TextFieldWidget
		private val editButton: ButtonWidget
		private val deleteButton: ButtonWidget
		private val client: MinecraftClient = MinecraftClient.getInstance()
		private var duplicate = false

		init {
			this.slot = TextFieldWidget(client.textRenderer, 120, 20, Text.of(bind.slot.toString()))
			this.slot.text = bind.slot.toString()
			this.slot.setChangedListener {
				if (it.isEmpty()) return@setChangedListener
				bind.slot = Integer.parseInt(it)
				this.update()
			}

			this.editButton = ButtonWidget.builder(Text.of("")) { button: ButtonWidget? ->
				parent?.selectedBind = bind
				this.update()
			}.dimensions(0, 0, 75, 20).build()

			this.deleteButton = ButtonWidget.builder(Text.of("Delete")) { button: ButtonWidget? ->
				removeEntry(this)
				this.update()
				InventoryBindsHandler.loadedInventoryBinds[parent?.inventory]?.removeIf { it == bind }
			}.dimensions(0, 0, 50, 20).build()

			this.update()
		}

		override fun render(context: DrawContext, mouseX: Int, mouseY: Int, hovered: Boolean, tickProgress: Float) {
			val i: Int = scrollbarX - this.deleteButton.width - 10
			val j = y - 2
			this.deleteButton.setPosition(i, j)
			this.deleteButton.render(context, mouseX, mouseY, tickProgress)
			val k = i - 5 - this.editButton.width
			this.editButton.setPosition(k, j)
			this.editButton.render(context, mouseX, mouseY, tickProgress)
			val l = k - 90 - this.slot.width
			this.slot.setPosition(l, j)
			this.slot.render(context, mouseX, mouseY, tickProgress)

			if (this.duplicate) {
				val m = this.editButton.x - 6
				context.fill(m, y - 1, m + 3, y + contentHeight, -65536)
			}
		}

		override fun children(): MutableList<out Element?> {
			return ImmutableList.of(this.slot, this.editButton, this.deleteButton)
		}

		override fun selectableChildren(): MutableList<out Selectable?> {
			return ImmutableList.of(this.slot, this.editButton, this.deleteButton)
		}

		override fun update() {
			this.editButton.message = this.bind.getBoundKeyText()
			this.duplicate = false
			val mutableText = Text.empty()

			if (!this.bind.isUnbound()) {

				for (bind in InventoryBindsHandler.loadedInventoryBinds[parent?.inventory]!!) {
					if (bind !== this.bind && this.bind.key == bind.key) {
						if (this.duplicate) {
							mutableText.append(", ")
						}

						this.duplicate = true
						mutableText.append(bind.key.toString())
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

			if (parent?.selectedBind === this.bind) {
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