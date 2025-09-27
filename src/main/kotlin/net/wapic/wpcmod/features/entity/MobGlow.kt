package net.wapic.wpcmod.features.entity

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.minecraft.entity.Entity
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.mob.MagmaCubeEntity
import net.minecraft.entity.mob.ShulkerEntity
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.PlayerEntity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.commands.TagCommand
import net.wapic.wpcmod.util.*
import net.wapic.wpcmod.util.EntityUtils.headTexture

object MobGlow {

	data class GlowOptions(var shouldGlow: Boolean, var color: ChromaColour)

	private val NO_GLOW = GlowOptions(false, ChromaColour(1f, 1f, 1f, 0, 0xff))

	private val miniBosses: List<String> = listOf("Lost Adventurer", "Shadow Assassin", "Diamond Guy")

	private val config get() = WpcMod.config

	fun computeGlow(entity: Entity): GlowOptions {

		if (TagCommand.players.contains(entity.name.string.lowercase()) || EntityUtils.isTagged(entity)) {
			return GlowOptions(config.general.esp.tag.glow, config.general.esp.tag.color)
		}

		if(Utils.getLocation() == Island.HUB) {
			return when (entity) {
				is ArmorStandEntity -> GlowOptions(
					config.general.esp.rat.glow && entity.headTexture == HeadTextures.RAT,
					config.general.esp.rat.color
				)
				else -> NO_GLOW
			}
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

				is PlayerEntity -> {
					if (miniBosses.contains(entity.name.string)) {
						GlowOptions(config.dungeon.esp.miniboss.glow, config.dungeon.esp.miniboss.color)
					} else if (EntityUtils.isStarredMob(entity)) {
						GlowOptions(config.dungeon.esp.starMob.glow, config.dungeon.esp.starMob.color)
					} else {
						NO_GLOW
					}
				}

				is ArmorStandEntity -> GlowOptions(
					config.dungeon.esp.starMob.glow && entity.isMarker && entity.headTexture == HeadTextures.FEL,
					config.dungeon.esp.starMob.color
				)

				else -> GlowOptions(
					EntityUtils.isStarredMob(entity) && config.dungeon.esp.starMob.glow,
					config.dungeon.esp.starMob.color
				)
			}
		}

		return NO_GLOW
	}
}