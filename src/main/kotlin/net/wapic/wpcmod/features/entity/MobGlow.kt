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
import net.wapic.wpcmod.util.ItemUtils.headTexture
import net.wapic.wpcmod.util.KuudraUtils
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
			return GlowOptions(shouldGlow = true, config.general.tagColor)
		}

		if (Utils.getLocation() == Island.GALATEA) {
			return when (entity) {
				is ShulkerEntity -> GlowOptions(
					config.galatea.esp.shulker.glow, config.galatea.esp.shulker.color
				)

				is AxolotlEntity -> GlowOptions(
					config.galatea.esp.axolotl.glow, config.galatea.esp.axolotl.color
				)

				is FrogEntity -> GlowOptions(
					config.galatea.esp.frog.glow, config.galatea.esp.frog.color
				)

				is PandaEntity -> GlowOptions(
					config.galatea.esp.panda.glow, config.galatea.esp.panda.color
				)

				is PufferfishEntity -> GlowOptions(
					config.galatea.esp.pufferfish.glow, config.galatea.esp.pufferfish.color
				)

				is TurtleEntity -> GlowOptions(
					config.galatea.esp.shellwise.glow, config.galatea.esp.shellwise.color
				)

				else -> NO_GLOW
			}
		}

		if (Utils.getLocation() == Island.END) {
			return when (entity) {
				is EnderDragonEntity -> GlowOptions(
					config.end.esp.dragon.glow, config.end.esp.dragon.color
				)

				else -> NO_GLOW
			}
		}

		if (Utils.getLocation() == Island.KUUDRA) {
			return when (entity) {
				is MagmaCubeEntity -> GlowOptions(
					config.kuudra.esp.kuudra.glow && entity == KuudraUtils.kuudraEntity,
					config.kuudra.esp.kuudra.color
				)

				else -> NO_GLOW
			}
		}

		if (Utils.getLocation() == Island.DUNGEON) {
			return when (entity) {
				is BatEntity -> GlowOptions(
					config.dungeon.esp.bat.glow, config.dungeon.esp.bat.color
				)

				is PlayerEntity -> GlowOptions(
					config.dungeon.esp.miniboss.glow && miniBosses.contains(
						entity.name.string
					), config.dungeon.esp.miniboss.color
				)

				is ArmorStandEntity -> GlowOptions(
					config.dungeon.esp.starMob.glow && entity.isMarker && entity.getEquippedStack(
						EquipmentSlot.HEAD
					).headTexture == FEL_HEAD_TEXTURE, config.dungeon.esp.starMob.color
				)

				else -> GlowOptions(
					isStarredMob(entity) && config.dungeon.esp.starMob.glow,
					config.dungeon.esp.starMob.color
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