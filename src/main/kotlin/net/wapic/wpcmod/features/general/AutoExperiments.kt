package net.wapic.wpcmod.features.general

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.slot.SlotActionType
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import org.lwjgl.glfw.GLFW

class AutoExperiments {

    private val config get() = WpcMod.config.instance.generalConfig.experimentSettings

    private var currentExperiment = ExperimentType.NONE

    private var ultrasequencerOrder = HashMap<Int, Int>()
    private val chronomatronOrder = ArrayList<Int>(28)

    private var hasAdded = false
    private var lastAdded = 0

    private var clicks = 0
    private var lastClickTime = 0L

    private var handledScreen: GenericContainerScreenHandler? = null

    private val ultraSequenceItems = listOf<Item>(
        Items.WHITE_DYE, Items.BROWN_DYE, Items.BLACK_DYE, Items.BLUE_DYE, Items.GRAY_DYE, Items.LIGHT_GRAY_DYE, //Unused dyes but we leave em here just in case
        Items.BONE_MEAL, Items.LAPIS_LAZULI, Items.RED_DYE,
        Items.GREEN_DYE,Items.CYAN_DYE, Items.LIGHT_BLUE_DYE,
        Items.LIME_DYE, Items.MAGENTA_DYE, Items.ORANGE_DYE,
        Items.PINK_DYE, Items.PURPLE_DYE, Items.YELLOW_DYE
    )

    init {
        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->  onScreenInit(screen) }
    }

    private fun reset(){
        currentExperiment = ExperimentType.NONE
        ultrasequencerOrder.clear()
        chronomatronOrder.clear()
        hasAdded = false
        lastAdded = 0
    }

    private fun onScreenInit(screen: Screen){
        reset()

        if(Utils.getLocation() != Island.PRIVATE_ISLAND && !config.autoExperiments) return
        handledScreen = (screen as? GenericContainerScreen)?.screenHandler ?: return

        currentExperiment = when {
            screen.title.string.startsWith("Chronomatron") -> ExperimentType.CHRONOMATRON
            screen.title.string.startsWith("Ultrasequencer") -> ExperimentType.ULTRASEQUENCER
            else -> ExperimentType.NONE
        }


        ScreenEvents.afterRender(screen).register { screen, drawContext, mouseX, mouseY, tickDelta -> onScreenRender(screen) }
    }

    private fun onScreenRender(screen: Screen){
       if(Utils.getLocation() != Island.PRIVATE_ISLAND && !config.autoExperiments) return

        (screen as? GenericContainerScreen)?.screenHandler?.inventory?.takeIf { it.size() >= 54 }?.let {
            when (currentExperiment) {
                ExperimentType.CHRONOMATRON -> solveChronomatron(it)
                ExperimentType.ULTRASEQUENCER -> solveUltrasequencer(it)
                else -> return
            }
        }
    }

    private fun solveChronomatron(inventory: Inventory){
        if(inventory.getStack(49).item == Blocks.GLOWSTONE.asItem() && !inventory.getStack(lastAdded).hasGlint()) {
            hasAdded = false
            if(config.autoClose && chronomatronOrder.size > 11 - config.serumCount) MinecraftClient.getInstance().currentScreen?.close()
        }

        if(!hasAdded && inventory.getStack(49).item == Items.CLOCK) {
            inventory.withIndex().find { (i, stack) -> i in 9..44 && stack.hasGlint() }?.let {
                chronomatronOrder.add(it.index)
                lastAdded = it.index
                hasAdded = true
                clicks = 0
            }
        }

        if(hasAdded && inventory.getStack(49).item  == Items.CLOCK && chronomatronOrder.size > clicks && System.currentTimeMillis() - lastClickTime > config.clickDelay) {
            handledScreen?.let {
                MinecraftClient.getInstance().interactionManager?.clickSlot(it.syncId, chronomatronOrder[clicks], GLFW.GLFW_MOUSE_BUTTON_LEFT, SlotActionType.PICKUP, MinecraftClient.getInstance().player)
                lastClickTime = System.currentTimeMillis()
                clicks++
            }
        }
    }

    private fun solveUltrasequencer(inventory: Inventory){
        if(inventory.getStack(49).item == Items.CLOCK) hasAdded = false

        if(!hasAdded && inventory.getStack(49).item == Blocks.GLOWSTONE.asItem()) {
            if(inventory.getStack(44) == Items.AIR) return
            ultrasequencerOrder.clear()
            inventory.withIndex().forEach { (i, stack) ->
                if(i in 9..44 && ultraSequenceItems.contains(stack.item)) ultrasequencerOrder[stack.count - 1] = i
            }
            hasAdded = true
            clicks = 0
            if(config.autoClose && ultrasequencerOrder.size > 9 - config.serumCount) MinecraftClient.getInstance().currentScreen?.close()
        }

        if(inventory.getStack(49).item  == Items.CLOCK && ultrasequencerOrder.contains(clicks) && System.currentTimeMillis() - lastClickTime > config.clickDelay) {
            handledScreen?.let { screenHandler ->
                ultrasequencerOrder[clicks]?.let {
                    MinecraftClient.getInstance().interactionManager?.clickSlot(screenHandler.syncId, it, GLFW.GLFW_MOUSE_BUTTON_LEFT, SlotActionType.PICKUP, MinecraftClient.getInstance().player)
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