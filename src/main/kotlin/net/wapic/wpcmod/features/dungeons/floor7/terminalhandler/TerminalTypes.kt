package net.wapic.wpcmod.features.dungeons.floor7.terminalhandler

import net.wapic.wpcmod.features.dungeons.floor7.termGUI.RubixGui
import net.wapic.wpcmod.features.dungeons.floor7.termGUI.TermGui

enum class TerminalTypes(
    val windowName: String,
    val windowSize: Int,
    val width: Int,
) : Type {
    RUBIX("Change all to same color!", 45, 3) {
        override fun getGUI(): TermGui = RubixGui
    },
}

private interface Type {
    fun getGUI(): TermGui
}