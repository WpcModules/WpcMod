package net.wapic.wpcmod.compat

import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.screen.OverlayDecider
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.InteractionResult
import net.wapic.wpcmod.features.dungeons.floor7.terminals.AbstractTerminalScreen

class ReiCompatibility : REIClientPlugin {

	override fun registerScreens(registry: ScreenRegistry) {
		registry.registerDecider(object : OverlayDecider {
			override fun <R : Screen> isHandingScreen(screen: Class<R>): Boolean {
				return AbstractTerminalScreen::class.java.isAssignableFrom(screen)
			}

			override fun <R : Screen> shouldScreenBeOverlaid(screen: R): InteractionResult {
				return InteractionResult.FAIL
			}
		})
	}
}