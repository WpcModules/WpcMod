package net.wapic.wpcmod.features.inventory.experiments

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.Container
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import org.lwjgl.glfw.GLFW

object AutoExperiments {

	private val config get() = WpcMod.config.inventory.experiments

	private var currentExperiment = ExperimentType.NONE

	private var ultrasequencerOrder = HashMap<Int, Int>()
	private val chronomatronOrder = ArrayList<Int>(28)

	private var hasAdded = false
	private var lastAdded = 0

	private var clicks = 0
	private var lastClickTime = 0L

	private var handledScreen: ChestMenu? = null

	private val ultraSequenceItems = listOf<Item>(
		Items.WHITE_DYE,
		Items.BROWN_DYE,
		Items.BLACK_DYE,
		Items.BLUE_DYE,
		Items.GRAY_DYE,
		Items.LIGHT_GRAY_DYE,
		Items.BONE_MEAL,
		Items.LAPIS_LAZULI,
		Items.RED_DYE,
		Items.GREEN_DYE,
		Items.CYAN_DYE,
		Items.LIGHT_BLUE_DYE,
		Items.LIME_DYE,
		Items.MAGENTA_DYE,
		Items.ORANGE_DYE,
		Items.PINK_DYE,
		Items.PURPLE_DYE,
		Items.YELLOW_DYE
	)

	fun init() {
		ScreenEvents.AFTER_INIT.register { _, screen, _, _ -> onScreenInit(screen) }
	}

	private fun reset() {
		currentExperiment = ExperimentType.NONE
		ultrasequencerOrder.clear()
		chronomatronOrder.clear()
		hasAdded = false
		lastAdded = 0
	}

	private fun onScreenInit(screen: Screen) {
		reset()

		if (Utils.getLocation() != Island.PRIVATE_ISLAND || !config.autoExperiments) return
		handledScreen = (screen as? ContainerScreen)?.menu ?: return

		currentExperiment = when {
			screen.title.string.startsWith("Chronomatron") -> ExperimentType.CHRONOMATRON
			screen.title.string.startsWith("Ultrasequencer") -> ExperimentType.ULTRASEQUENCER
			else -> ExperimentType.NONE
		}


		ScreenEvents.afterRender(screen).register { screen, _, _, _, _ -> onScreenRender(screen) }
	}

	private fun onScreenRender(screen: Screen) {
		if (Utils.getLocation() != Island.PRIVATE_ISLAND || !config.autoExperiments) return

		(screen as? ContainerScreen)?.menu?.container?.takeIf { it.containerSize >= 54 }?.let {
			when (currentExperiment) {
				ExperimentType.CHRONOMATRON -> solveChronomatron(it)
				ExperimentType.ULTRASEQUENCER -> solveUltrasequencer(it)
				else -> return
			}
		}
	}

	private fun solveChronomatron(inventory: Container) {
		if (inventory.getItem(49).item == Blocks.GLOWSTONE.asItem() && !inventory.getItem(lastAdded).hasFoil()) {
			hasAdded = false
			if (config.autoClose && chronomatronOrder.size > 11 - config.serumCount) Minecraft.getInstance().screen?.onClose()
		}

		if (!hasAdded && inventory.getItem(49).item == Items.CLOCK) {
			inventory.withIndex().find { (i, stack) -> i in 9..44 && stack.hasFoil() }?.let {
				chronomatronOrder.add(it.index)
				lastAdded = it.index
				hasAdded = true
				clicks = 0
			}
		}

		if (hasAdded && inventory.getItem(49).item == Items.CLOCK && chronomatronOrder.size > clicks && System.currentTimeMillis() - lastClickTime > config.clickDelay) {
			handledScreen?.let {
				MC.gameMode?.handleInventoryMouseClick(
					it.containerId,
					chronomatronOrder[clicks],
					GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
					ClickType.CLONE,
					MC.player ?: return
				)
				lastClickTime = System.currentTimeMillis()
				clicks++
			}
		}
	}

	private fun solveUltrasequencer(inventory: Container) {
		if (inventory.getItem(49).item == Items.CLOCK) hasAdded = false

		if (!hasAdded && inventory.getItem(49).item == Blocks.GLOWSTONE.asItem()) {
			if (inventory.getItem(44) == Items.AIR) return
			ultrasequencerOrder.clear()
			inventory.withIndex().forEach { (i, stack) ->
				if (i in 9..44 && ultraSequenceItems.contains(stack.item)) ultrasequencerOrder[stack.count - 1] = i
			}
			hasAdded = true
			clicks = 0
			if (config.autoClose && ultrasequencerOrder.size > 9 - config.serumCount) Minecraft.getInstance().screen?.onClose()
		}

		if (inventory.getItem(49).item == Items.CLOCK && ultrasequencerOrder.contains(clicks) && System.currentTimeMillis() - lastClickTime > config.clickDelay) {
			handledScreen?.let { screenHandler ->
				ultrasequencerOrder[clicks]?.let {
					MC.gameMode?.handleInventoryMouseClick(
						screenHandler.containerId,
						it,
						GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
						ClickType.CLONE,
						MC.player ?: return
					)
				}
				lastClickTime = System.currentTimeMillis()
				clicks++
			}
		}
	}

	private enum class ExperimentType {
		CHRONOMATRON,
		ULTRASEQUENCER,
		NONE
	}
}