package net.wapic.wpcmod.features.dungeons.floor7.termsim

import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSolver
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.StartsWithHandler
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalTypes
import net.wapic.wpcmod.util.ChatUtils
import kotlin.math.floor

class StartsWithSim(
	private val letter: String = listOf("A", "B", "C", "G", "D", "M", "N", "R", "S", "T", "W").random()
) : TermSimGUI(
	"What starts with: \'$letter\'?", TerminalTypes.STARTS_WITH.windowSize
) {

    override fun create() {
        createNewGui {
            when {
                floor(it.containerSlot / 9f) !in 1f..3f || it.containerSlot % 9 !in 1..7 -> blackPane
                it.containerSlot == (10..16).random() -> getLetterItemStack()
                Math.random() > .7f -> getLetterItemStack()
                else -> getLetterItemStack(true)
            }
        }
    }

	override fun slotClick(slot: Slot, button: Int) {
		val stack = slot.item
		if (!stack.hoverName.string.startsWith(
				letter,
				true
			)
		) return ChatUtils.sendMessage("§cThat item does not start with: \'$letter\' ${slot.item.hoverName.string}!")

		createNewGui {
			if (it == slot) stack.apply { set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true) } else it.item
		}

        playTermSimSound()

		val isCompleted = guiInventorySlots.none {
			(TerminalSolver.currentTerm as? StartsWithHandler)?.let { handler ->
				return@none it.index !in handler.clickedSlots && it.item.hoverName.string.startsWith(letter, true)
			}
			return@none false
		}
		if (isCompleted) {
			this@StartsWithSim.onTerminalSolved()
		}
    }

    private fun getLetterItemStack(filterNot: Boolean = false): ItemStack {
		val matchingItem = BuiltInRegistries.ITEM.filter { item ->
			val id = item.defaultInstance.hoverName.string
			id.startsWith(letter, true) != filterNot && !id.contains("pane", true)
		}.random()

        return ItemStack(matchingItem)
    }
}