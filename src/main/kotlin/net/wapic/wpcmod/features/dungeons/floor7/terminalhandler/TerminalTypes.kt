package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.wapic.wpcmod.features.dungeons.floor7.termGUI.MelodyGui
import net.wapic.wpcmod.features.dungeons.floor7.termGUI.NumbersGui
import net.wapic.wpcmod.features.dungeons.floor7.termGUI.PanesGui
import net.wapic.wpcmod.features.dungeons.floor7.termGUI.RubixGui
import net.wapic.wpcmod.features.dungeons.floor7.termGUI.SelectAllGui
import net.wapic.wpcmod.features.dungeons.floor7.termGUI.StartsWithGui
import net.wapic.wpcmod.features.dungeons.floor7.termGUI.TermGui
import net.wapic.wpcmod.features.dungeons.floor7.termsim.MelodySim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.NumbersSim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.PanesSim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.RubixSim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.SelectAllSim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.StartsWithSim
import net.wapic.wpcmod.features.dungeons.floor7.termsim.TermSimGUI

enum class TerminalTypes(
    val windowName: String,
    val windowSize: Int
) : Type {
    PANES("Correct all the panes!", 45) {
        override fun getSimulator() = PanesSim
        override fun getGUI(): TermGui = PanesGui
    },
    RUBIX("Change all to same color!", 45) {
        override fun getSimulator() = RubixSim
        override fun getGUI(): TermGui = RubixGui
    },
    NUMBERS("Click in order!", 36) {
        override fun getSimulator() = NumbersSim
        override fun getGUI(): TermGui = NumbersGui
    },
    STARTS_WITH("What starts with:", 45) {
        override fun getSimulator() = StartsWithSim()
        override fun getGUI(): TermGui = StartsWithGui
    },
    SELECT("Select all the", 54) {
        override fun getSimulator() = SelectAllSim()
        override fun getGUI(): TermGui = SelectAllGui
    },
    MELODY("Click the button on time!", 54) {
        override fun getSimulator() = MelodySim
        override fun getGUI(): TermGui = MelodyGui
    },
}

private interface Type {
    fun getSimulator(): TermSimGUI
    fun getGUI(): TermGui
}