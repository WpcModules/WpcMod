package net.wapic.wpcmod.features.dungeons.floor7

import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalTypes
import net.wapic.wpcmod.features.dungeons.floor7.termsim.*

object TerminalSimulator {

    fun openRandomTerminal(ping: Long = 0L) {
        when (TerminalTypes.entries.random()) {
            TerminalTypes.STARTS_WITH -> StartsWithSim().open(ping)
            TerminalTypes.PANES       -> PanesSim.open(ping)
            TerminalTypes.SELECT_ALL -> SelectAllSim().open(ping)
            TerminalTypes.NUMBERS -> NumbersSim.open(ping)
            TerminalTypes.MELODY      -> MelodySim.open(ping)
            TerminalTypes.RUBIX       -> RubixSim.open(ping)
        }
    }
}