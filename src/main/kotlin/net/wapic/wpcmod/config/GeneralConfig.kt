package net.wapic.wpcmod.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.general.shortcut.ShortcutScreen

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
}