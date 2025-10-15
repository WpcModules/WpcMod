package net.wapic.wpcmod.features.dungeons.floor7

import net.wapic.wpcmod.features.dungeons.floor7.terminalhandler.TerminalTypes
import net.wapic.wpcmod.features.dungeons.floor7.termsim.SelectAllSim
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.dungeons.floor7.termsim.MelodySim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.NumbersSim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.PanesSim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.RubixSim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.StartsWithSim

object TerminalSimulator {

    fun openRandomTerminal(ping: Long = 0L) {
        when (listOf(TerminalTypes.PANES, TerminalTypes.RUBIX, TerminalTypes.NUMBERS, TerminalTypes.STARTS_WITH, TerminalTypes.SELECT).random()) {
            TerminalTypes.STARTS_WITH -> StartsWithSim().open(ping)
            TerminalTypes.PANES       -> PanesSim.open(ping)
            TerminalTypes.SELECT      -> SelectAllSim().open(ping)
            TerminalTypes.NUMBERS       -> NumbersSim.open(ping)
            TerminalTypes.MELODY      -> MelodySim.open(ping)
            TerminalTypes.RUBIX       -> RubixSim.open(ping)
        }
    }
}