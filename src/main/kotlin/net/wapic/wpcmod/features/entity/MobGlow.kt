package net.wapic.wpcmod.features.entity

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.MagmaCubeEntity
import net.minecraft.entity.mob.ShulkerEntity
import net.minecraft.entity.passive.AxolotlEntity
import net.minecraft.entity.passive.FrogEntity
import net.minecraft.entity.passive.PandaEntity
import net.minecraft.entity.passive.PufferfishEntity
import net.wapic.wpcmod.config.ConfigManager
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils

object MobGlow {

    data class GlowOptions(var shouldGlow: Boolean, var color: ChromaColour)

    private val config get() = ConfigManager.config

    fun computeGlow(entity: Entity): GlowOptions {
        return when(entity) {
            // Galatea
            is ShulkerEntity -> GlowOptions(Utils.getLocation() == Island.GALATEA && config.galateaConfig.espSettings.shulkerSettings.glow, config.galateaConfig.espSettings.shulkerSettings.color)
            is AxolotlEntity -> GlowOptions(Utils.getLocation() == Island.GALATEA && config.galateaConfig.espSettings.axolotlSettings.glow, config.galateaConfig.espSettings.axolotlSettings.color)
            is FrogEntity -> GlowOptions(Utils.getLocation() == Island.GALATEA && config.galateaConfig.espSettings.frogSettings.glow, config.galateaConfig.espSettings.frogSettings.color)
            is PandaEntity -> GlowOptions(Utils.getLocation() == Island.GALATEA && config.galateaConfig.espSettings.pandaSettings.glow, config.galateaConfig.espSettings.pandaSettings.color)
            is PufferfishEntity -> GlowOptions(Utils.getLocation() == Island.GALATEA && config.galateaConfig.espSettings.pufferfishSettings.glow, config.galateaConfig.espSettings.pufferfishSettings.color)

            //Kuudra
            is MagmaCubeEntity -> GlowOptions(Utils.getLocation() == Island.KUUDRA && config.kuudraConfig.espSettings.kuudraSettings.glow && entity.size == 30, config.kuudraConfig.espSettings.kuudraSettings.color)

            //Default
            else -> GlowOptions(false, ChromaColour(1f, 1f, 1f, 0, 0xff))
        }
    }
}