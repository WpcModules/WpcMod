package net.wapic.wpcmod.features.entity

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.mob.MagmaCubeEntity
import net.minecraft.entity.mob.ShulkerEntity
import net.minecraft.entity.passive.AxolotlEntity
import net.minecraft.entity.passive.BatEntity
import net.minecraft.entity.passive.FrogEntity
import net.minecraft.entity.passive.PandaEntity
import net.minecraft.entity.passive.PufferfishEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.predicate.entity.EntityPredicates
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.ItemUtils.getHeadTexture
import net.wapic.wpcmod.util.Utils

object MobGlow {

    data class GlowOptions(var shouldGlow: Boolean, var color: ChromaColour)
    private val NO_GLOW = GlowOptions(false, ChromaColour(1f, 1f, 1f, 0, 0xff))

    private const val FEL_HEAD_TEXTURE: String = "ewogICJ0aW1lc3RhbXAiIDogMTcyMDAyNTQ4Njg2MywKICAicHJvZmlsZUlkIiA6ICIzZDIxZTYyMTk2NzQ0Y2QwYjM3NjNkNTU3MWNlNGJlZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTcl83MUJsYWNrYmlyZCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMjg2ZGFjYjBmMjE0NGQ3YTQxODdiZTM2YmJhYmU4YTk4ODI4ZjdjNzlkZmY1Y2UwMTM2OGI2MzAwMTU1NjYzIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="
    private val miniBosses: List<String> = listOf("Lost Adventurer","Shadow Assassin","Diamond Guy")

    private val config get() = WpcMod.config

    fun computeGlow(entity: Entity): GlowOptions {
        if(Utils.getLocation() == Island.GALATEA) {
            return when(entity) {
                is ShulkerEntity -> GlowOptions(config.galateaConfig.espSettings.shulkerSettings.glow, config.galateaConfig.espSettings.shulkerSettings.color)
                is AxolotlEntity -> GlowOptions(config.galateaConfig.espSettings.axolotlSettings.glow, config.galateaConfig.espSettings.axolotlSettings.color)
                is FrogEntity -> GlowOptions(config.galateaConfig.espSettings.frogSettings.glow, config.galateaConfig.espSettings.frogSettings.color)
                is PandaEntity -> GlowOptions(config.galateaConfig.espSettings.pandaSettings.glow, config.galateaConfig.espSettings.pandaSettings.color)
                is PufferfishEntity -> GlowOptions(config.galateaConfig.espSettings.pufferfishSettings.glow, config.galateaConfig.espSettings.pufferfishSettings.color)
                else -> NO_GLOW
            }
        }

        if(Utils.getLocation() == Island.END) {
            return when(entity) {
                is EnderDragonEntity -> GlowOptions(config.endConfig.espSettings.dragonSettings.glow, config.endConfig.espSettings.dragonSettings.color)
                else -> NO_GLOW
            }
        }

        if(Utils.getLocation() == Island.KUUDRA) {
            return when(entity) {
                is MagmaCubeEntity -> GlowOptions(config.kuudraConfig.espSettings.kuudraSettings.glow && entity.size == 30, config.kuudraConfig.espSettings.kuudraSettings.color)
                else -> NO_GLOW
            }
        }

        if(Utils.getLocation() == Island.DUNGEON) {
            return when(entity) {
                is BatEntity -> GlowOptions(config.dungeonConfig.espSettings.batESP.glow, config.dungeonConfig.espSettings.batESP.color)
                is PlayerEntity -> GlowOptions(config.dungeonConfig.espSettings.miniESP.glow && miniBosses.contains(entity.name.string), config.dungeonConfig.espSettings.miniESP.color)
                is ArmorStandEntity -> GlowOptions(config.dungeonConfig.espSettings.starMobESP.glow && entity.isMarker && entity.getEquippedStack(EquipmentSlot.HEAD).getHeadTexture() == FEL_HEAD_TEXTURE, config.dungeonConfig.espSettings.starMobESP.color)
                else -> GlowOptions(isStarredMob(entity) && config.dungeonConfig.espSettings.starMobESP.glow, config.dungeonConfig.espSettings.starMobESP.color)
            }
        }

        return NO_GLOW
    }

    private fun isStarredMob(entity: Entity): Boolean {
        val armorStands = entity.world.getEntitiesByClass(ArmorStandEntity::class.java, entity.boundingBox.expand(0.0, 2.0, 0.0), EntityPredicates.NOT_MOUNTED) ?: return false
        return armorStands.isNotEmpty() && armorStands.first()?.name?.string?.contains("✯") ?: false
    }
}