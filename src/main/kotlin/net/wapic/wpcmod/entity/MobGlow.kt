package net.wapic.wpcmod.entity

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.MagmaCubeEntity
import net.minecraft.entity.mob.ShulkerEntity
import net.minecraft.entity.passive.AxolotlEntity
import net.minecraft.entity.passive.FrogEntity
import net.minecraft.entity.passive.PandaEntity
import net.minecraft.entity.passive.PufferfishEntity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils

object MobGlow {

    data class GlowOptions(var shouldGlow: Boolean, var color: ChromaColour)

    private val config = WpcMod.config.instance

    fun computeGlow(entity: Entity): GlowOptions {
        when(entity) {
            // Galatea
            is ShulkerEntity -> return GlowOptions(Utils.getLocation() == Island.GALATEA && config.galateaConfig.espSettings.shulkerSettings.glow, config.galateaConfig.espSettings.shulkerSettings.color)
            is AxolotlEntity -> return GlowOptions(Utils.getLocation() == Island.GALATEA && config.galateaConfig.espSettings.axolotlSettings.glow, config.galateaConfig.espSettings.axolotlSettings.color)
            is FrogEntity -> return GlowOptions(Utils.getLocation() == Island.GALATEA && config.galateaConfig.espSettings.frogSettings.glow, config.galateaConfig.espSettings.frogSettings.color)
            is PandaEntity -> return GlowOptions(Utils.getLocation() == Island.GALATEA && config.galateaConfig.espSettings.pandaSettings.glow, config.galateaConfig.espSettings.pandaSettings.color)
            is PufferfishEntity -> return GlowOptions(Utils.getLocation() == Island.GALATEA && config.galateaConfig.espSettings.pufferfishSettings.glow, config.galateaConfig.espSettings.pufferfishSettings.color)

            //Kuudra
            is MagmaCubeEntity -> return GlowOptions(Utils.getLocation() == Island.KUUDRA && config.kuudraConfig.espSettings.kuudraSetings.glow && entity.size == 30, config.kuudraConfig.espSettings.kuudraSetings.color)
        }
        return GlowOptions(false, ChromaColour(1f, 1f, 1f, 0, 0xff))
    }

}