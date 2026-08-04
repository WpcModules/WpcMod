package net.wapic.wpcmod.compat

import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.screen.OverlayDecider
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.InteractionResult
import net.wapic.wpcmod.features.dungeons.floor7.TerminalSolver
import net.wapic.wpcmod.features.dungeons.floor7.terminals.AbstractTerminalScreen
import net.wapic.wpcmod.features.dungeons.floor7.termsim.RubixSim

class ReiCompatibility : REIClientPlugin {

	override fun registerScreens(registry: ScreenRegistry) {
		registry.registerDecider(object : OverlayDecider {
			override fun <R : Screen> isHandingScreen(screen: Class<R>): Boolean {
				return when (screen) {
					AbstractTerminalScreen::class.java -> true
					RubixSim::class.java -> true
					ContainerScreen::class.java -> true
					else -> false
				}
			}

			override fun <R : Screen> shouldScreenBeOverlaid(screen: R): InteractionResult {
				if (screen is ContainerScreen) {
					return if (TerminalSolver.currentTerm != null) InteractionResult.FAIL else InteractionResult.PASS
				}
				return InteractionResult.FAIL
			}
		})
	}
}