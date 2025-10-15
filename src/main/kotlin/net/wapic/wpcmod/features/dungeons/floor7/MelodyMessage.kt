package net.wapic.wpcmod.features.dungeons.floor7

import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalTypes
import net.wapic.wpcmod.features.dungeons.floor7.termsim.TermSimGUI
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.skyblock.DungeonEvents
import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalHandler
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils

object MelodyMessage {
	private val config get() = WpcMod.config.dungeon.floor7
    private const val MELODY_STARTED_MESSAGE = "Melody Terminal start!"

	fun init() {
		DungeonEvents.TERMINAL_OPENED.register(::onTermLoad)
	}

    fun onTermLoad(terminal: TerminalHandler) {
        if (DungeonUtils.getF7Phase() != DungeonUtils.F7Phase.GOLDOR || terminal.type != TerminalTypes.MELODY || MC.screen is TermSimGUI) return
        if (config.melodyMessage) Utils.runCommand("pc $MELODY_STARTED_MESSAGE")
    }

}