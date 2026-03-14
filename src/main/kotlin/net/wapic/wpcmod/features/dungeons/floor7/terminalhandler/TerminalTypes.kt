package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.wapic.wpcmod.features.dungeons.floor7.termGUI.*

enum class TerminalTypes(
    val windowName: String,
    val windowSize: Int,
    val width: Int,
) : Type {
    PANES("Correct all the panes!", 45, 5) {
        override fun getGUI(): TermGui = PanesGui
    },
    RUBIX("Change all to same color!", 45, 3) {
        override fun getGUI(): TermGui = RubixGui
    },
    NUMBERS("Click in order!", 36, 7) {
        override fun getGUI(): TermGui = NumbersGui
    },
    STARTS_WITH("What starts with:", 45, 7) {
        override fun getGUI(): TermGui = StartsWithGui
    },
    SELECT_ALL("Select all the", 54, 7) {
        override fun getGUI(): TermGui = SelectAllGui
    },
    MELODY("Click the button on time!", 54, 7) {
        override fun getGUI(): TermGui = MelodyGui
    },
}

private interface Type {
    fun getGUI(): TermGui
}