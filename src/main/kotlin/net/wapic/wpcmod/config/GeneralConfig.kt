package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.features.general.shortcut.ShortcutScreen

class GeneralConfig {

    @Expose
    @JvmField
    @ConfigOption(name = "Fullbright", desc = "Makes things not dark")
    @ConfigEditorBoolean
    var fullbright: Boolean = false

    @Expose
    @JvmField
    @ConfigOption(name = "Command Keybind Editor", desc = "Opens the screen to manage command keybinds")
    @ConfigEditorButton(buttonText = "Open")
    val runnable = Runnable {
        MinecraftClient.getInstance().setScreen(ShortcutScreen())
    }

    @Expose
    @JvmField
    @Category(name = "Experiments", desc = "Experimentation Config")
    var experimentSettings: ExperimentSettings = ExperimentSettings()

    class ExperimentSettings {

        @Expose
        @JvmField
        @ConfigOption(name = "Auto Solve Experiments", desc = "render a glow around the entity")
        @ConfigEditorBoolean
        var autoExperiments: Boolean = false

        @Expose
        @JvmField
        @ConfigOption(name = "Auto Close", desc = "Auto Close Experiment when done")
        @ConfigEditorBoolean
        var autoClose: Boolean = false

        @Expose
        @JvmField
        @ConfigOption(name = "Click delay", desc = "Delay between clicks on experiments")
        @ConfigEditorSlider(minStep = 1f, minValue = 200f, maxValue = 1000f)
        var clickDelay: Float = 250f

        @Expose
        @JvmField
        @ConfigOption(name = "Metaphysical Serum", desc = "Sets how many Metaphysical serums have been consumed")
        @ConfigEditorSlider(minStep = 1f, minValue = 0f, maxValue = 3f)
        var serumCount: Float = 0f
    }
}