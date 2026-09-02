package net.wapic.wpcmod.features.dungeons.floor7.terminals

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents.NOTE_BLOCK_PLING
import net.minecraft.sounds.SoundSource
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.events.SoundEvents
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.terminals.simulator.*
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.MC
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object Terminal {

	private val config get() = WpcMod.config.dungeon.floor7.terminalSolvers
	private val SOLVED_REGEX = Regex("^solved a terminal!$") //TODO: get actual regex

	val STARTS_WITH_PATTERN = Regex("^What starts with: '(\\w)'\\?$")
	val SELECT_ALL_PATTERN = Regex("^Select all the (.+) items!$")
	val RUBIX_ORDER = listOf(
		Items.ORANGE_STAINED_GLASS_PANE,
		Items.YELLOW_STAINED_GLASS_PANE,
		Items.GREEN_STAINED_GLASS_PANE,
		Items.BLUE_STAINED_GLASS_PANE,
		Items.RED_STAINED_GLASS_PANE,
	)

	var handler: TerminalSimulatorHandler? = null
		private set
	private var lastTerminal: Type? = null

	fun init() {
		GuiEvents.SLOT_UPDATE.register(::onSlotUpdate)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		SoundEvents.PLAY.register(::onPlaySound)
	}

	private fun onPlaySound(
		sound: Holder<SoundEvent>,
		source: SoundSource,
		pos: Vec3,
		volume: Float,
		pitch: Float,
		seed: Long,
		level: ClientLevel,
		callbackInfo: CallbackInfo
	) {
		if (MC.screen !is AbstractTerminalScreen || config.soundReplacement.isEmpty()) return
		if (sound != NOTE_BLOCK_PLING || volume != 8f || pitch != 4.047619f) return
		val customSound = BuiltInRegistries.SOUND_EVENT.find { it.location.path == config.soundReplacement }
			?: return WpcMod.LOGGER.error("Unable to find sound from: {}", config.soundReplacement)
		callbackInfo.cancel()
		MC.playSound(customSound, config.soundVolume, config.soundPitch)

	}

	private fun onSlotUpdate(containerId: Int, slotId: Int, itemStack: ItemStack) {
		val screen = MC.screen as? AbstractTerminalScreen ?: return
		if (containerId != screen.menu.containerId) return
		screen.slotChanged(screen.menu, slotId, itemStack)
	}

	private fun onMessageReceived(message: Component, isActionBar: Boolean) {
		if (isActionBar || !DungeonUtils.inDungeons) return

		if (message.string.matches(SOLVED_REGEX)) {
			DungeonEvents.TERMINAL_SOLVED.invoker().onSolve(lastTerminal ?: return)
			lastTerminal = null
		}
	}

	fun createSolverScreen(menu: ChestMenu, title: Component) {
		val terminalType = Type.fromTitle(title) ?: return
		lastTerminal = terminalType
		MC.screen = terminalType.screenFactory(menu, title)
		WpcMod.LOGGER.debug("Opened custom menu {}, screen: {}", menu.containerId, MC.screen)
	}

	fun shouldReplace(title: Component): Boolean {
		return config.enabled && Type.entries.any { title.string.startsWith(it.windowName) }
	}

	fun openSimulator(type: Type = Type.entries.random()) {
		val player = MC.player ?: return

		val title = Component.literal(type.windowName)
		if (type == Type.STARTS_WITH) title.append(" '${"ABCDEFGHIJLMNOW".random()}'?")
		if (type == Type.SELECT_ALL) title.append(
			" ${DyeColor.entries.random().name.uppercase().replace("_", " ")} items!"
		)

		val menuType = when (type) {
			Type.NUMBERS -> MenuType.GENERIC_9x4
			Type.PANES -> MenuType.GENERIC_9x5
			Type.RUBIX -> MenuType.GENERIC_9x5
			Type.STARTS_WITH -> MenuType.GENERIC_9x5
			Type.SELECT_ALL -> MenuType.GENERIC_9x6
			Type.MELODY -> MenuType.GENERIC_9x6
		}

		val inventory = player.inventory
		val menu = menuType.create(Int.MAX_VALUE, inventory)
		handler = type.simulatorFactory(menu, title)

		player.containerMenu = menu
		MC.screen =
			if (config.enabled) type.screenFactory(menu, title) else TerminalSimulatorScreen(menu, inventory, title)
	}

	fun removeSimulator() {
		handler = null
	}

	enum class Type(
		val windowName: String,
		val screenFactory: (menu: ChestMenu, title: Component) -> Screen,
		val simulatorFactory: (menu: ChestMenu, title: Component) -> TerminalSimulatorHandler
	) {

		PANES("Correct all the panes!", ::PanesTerminalScreen, ::PanesSimulatorHandler),
		RUBIX("Change all to same color!", ::RubixTerminalScreen, ::RubixSimulatorHandler),
		NUMBERS("Click in order!", ::NumbersTerminalScreen, ::NumbersSimulatorHandler),
		STARTS_WITH("What starts with:", ::StartsWithTerminalScreen, ::StartsWithSimulatorHandler),
		SELECT_ALL("Select all the", ::SelectAllTerminalScreen, ::SelectAllSimulatorHandler),
		MELODY("Click the button on time!", ::MelodyTerminalScreen, ::MelodySimulatorHandler);

		companion object {

			fun fromTitle(title: Component): Type? {
				return entries.firstOrNull { title.string.startsWith(it.windowName) }
			}
		}
	}
}