package net.wapic.wpcmod.config

import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.features.general.shortcut.ShortcutScreen

class GeneralConfig {

    @ConfigOption(name = "Fullbright", desc = "Makes things not dark")
    @ConfigEditorBoolean
    var fullbright: Boolean = false

    @ConfigOption(name = "Armor Swapper", desc = "Instantly swap to Sorrow when Keybind is hit")
    @ConfigEditorBoolean
    var armorSwapper: Boolean = false

    @Transient
    @ConfigOption(name = "Command Keybind Editor", desc = "Opens the screen to manage command keybinds")
    @ConfigEditorButton(buttonText = "Open")
    val shortcutEditor = Runnable {
        MinecraftClient.getInstance().setScreen(ShortcutScreen())
    }

    @Category(name = "Experiments", desc = "Experimentation Config")
    var experimentSettings: ExperimentSettings = ExperimentSettings()

    class ExperimentSettings {

        @ConfigOption(name = "Auto Solve Experiments", desc = "Completes Chronomatron and Ultrasequencer automatically")
        @ConfigEditorBoolean
        var autoExperiments: Boolean = false

        @ConfigOption(name = "Auto Close", desc = "Auto Close Experiment when done")
        @ConfigEditorBoolean
        var autoClose: Boolean = false

        @ConfigOption(name = "Click delay", desc = "Delay between clicks on experiments")
        @ConfigEditorSlider(minStep = 1.0f, minValue = 200.0f, maxValue = 500.0f)
        var clickDelay: Float = 250.0f

        @ConfigOption(name = "Metaphysical Serum", desc = "Sets how many Metaphysical serums have been consumed")
        @ConfigEditorSlider(minStep = 1.0f, minValue = 0.0f, maxValue = 3.0f)
        var serumCount: Float = 0.0f
    }
}