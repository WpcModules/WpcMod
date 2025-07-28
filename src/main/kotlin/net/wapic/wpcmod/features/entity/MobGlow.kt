package net.wapic.wpcmod.features.entity

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.mob.MagmaCubeEntity
import net.minecraft.entity.mob.ShulkerEntity
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.predicate.entity.EntityPredicates
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.commands.TagCommand
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.ItemUtils.getHeadTexture
import net.wapic.wpcmod.util.Utils

object MobGlow {

	data class GlowOptions(var shouldGlow: Boolean, var color: ChromaColour)

	private val NO_GLOW = GlowOptions(false, ChromaColour(1f, 1f, 1f, 0, 0xff))

	private const val FEL_HEAD_TEXTURE: String =
		"ewogICJ0aW1lc3RhbXAiIDogMTcyMDAyNTQ4Njg2MywKICAicHJvZmlsZUlkIiA6ICIzZDIxZTYyMTk2NzQ0Y2QwYjM3NjNkNTU3MWNlNGJlZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTcl83MUJsYWNrYmlyZCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMjg2ZGFjYjBmMjE0NGQ3YTQxODdiZTM2YmJhYmU4YTk4ODI4ZjdjNzlkZmY1Y2UwMTM2OGI2MzAwMTU1NjYzIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="
	private val miniBosses: List<String> = listOf("Lost Adventurer", "Shadow Assassin", "Diamond Guy")

	private val config get() = WpcMod.config

	fun computeGlow(entity: Entity): GlowOptions {

		if (TagCommand.players.contains(entity.name.string.lowercase()) || isTagged(entity)) {
			return GlowOptions(shouldGlow = true, config.generalConfig.tagColor)
		}

		if (Utils.getLocation() == Island.GALATEA) {
			return when (entity) {
				is ShulkerEntity -> GlowOptions(
					config.galateaConfig.esp.shulker.glow, config.galateaConfig.esp.shulker.color
				)

				is AxolotlEntity -> GlowOptions(
					config.galateaConfig.esp.axolotl.glow, config.galateaConfig.esp.axolotl.color
				)

				is FrogEntity -> GlowOptions(
					config.galateaConfig.esp.frog.glow, config.galateaConfig.esp.frog.color
				)

				is PandaEntity -> GlowOptions(
					config.galateaConfig.esp.panda.glow, config.galateaConfig.esp.panda.color
				)

				is PufferfishEntity -> GlowOptions(
					config.galateaConfig.esp.pufferfish.glow, config.galateaConfig.esp.pufferfish.color
				)

				is TurtleEntity -> GlowOptions(
					config.galateaConfig.esp.shellwise.glow, config.galateaConfig.esp.shellwise.color
				)

				else -> NO_GLOW
			}
		}

		if (Utils.getLocation() == Island.END) {
			return when (entity) {
				is EnderDragonEntity -> GlowOptions(
					config.endConfig.esp.dragon.glow, config.endConfig.esp.dragon.color
				)

				else -> NO_GLOW
			}
		}

		if (Utils.getLocation() == Island.KUUDRA) {
			return when (entity) {
				is MagmaCubeEntity -> GlowOptions(
					config.kuudraConfig.esp.kuudra.glow && entity.size == 30, config.kuudraConfig.esp.kuudra.color
				)

				else -> NO_GLOW
			}
		}

		if (Utils.getLocation() == Island.DUNGEON) {
			return when (entity) {
				is BatEntity -> GlowOptions(
					config.dungeonConfig.esp.bat.glow, config.dungeonConfig.esp.bat.color
				)

				is PlayerEntity -> GlowOptions(
					config.dungeonConfig.esp.miniboss.glow && miniBosses.contains(
						entity.name.string
					), config.dungeonConfig.esp.miniboss.color
				)

				is ArmorStandEntity -> GlowOptions(
					config.dungeonConfig.esp.starMob.glow && entity.isMarker && entity.getEquippedStack(
						EquipmentSlot.HEAD
					).getHeadTexture() == FEL_HEAD_TEXTURE, config.dungeonConfig.esp.starMob.color
				)

				else -> GlowOptions(
					isStarredMob(entity) && config.dungeonConfig.esp.starMob.glow,
					config.dungeonConfig.esp.starMob.color
				)
			}
		}

		return NO_GLOW
	}

	private fun getArmorStandsByEntity(entity: Entity): List<ArmorStandEntity> {
		return entity.world.getEntitiesByClass(
			ArmorStandEntity::class.java, entity.boundingBox.expand(0.0, 2.0, 0.0), EntityPredicates.NOT_MOUNTED
		)
	}

	private fun isTagged(entity: Entity): Boolean {
		val armorStands = getArmorStandsByEntity(entity)
		return armorStands.isNotEmpty() && TagCommand.players.find {
			armorStands.first().name?.string?.lowercase()?.contains(it) ?: false
		}?.isNotEmpty() ?: false
	}

	private fun isStarredMob(entity: Entity): Boolean {
		val armorStands = getArmorStandsByEntity(entity)
		return armorStands.isNotEmpty() && armorStands.first().name?.string?.contains("✯") ?: false
	}
}