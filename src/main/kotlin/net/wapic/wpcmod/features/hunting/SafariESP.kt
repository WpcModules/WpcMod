package net.wapic.wpcmod.features.hunting

import net.minecraft.client.player.RemotePlayer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.animal.armadillo.Armadillo
import net.minecraft.world.entity.animal.bee.Bee
import net.minecraft.world.entity.animal.dolphin.Dolphin
import net.minecraft.world.entity.animal.fish.TropicalFish
import net.minecraft.world.entity.animal.fox.Fox
import net.minecraft.world.entity.animal.frog.Frog
import net.minecraft.world.entity.animal.goat.Goat
import net.minecraft.world.entity.animal.golem.SnowGolem
import net.minecraft.world.entity.animal.panda.Panda
import net.minecraft.world.entity.animal.parrot.Parrot
import net.minecraft.world.entity.animal.polarbear.PolarBear
import net.minecraft.world.entity.animal.sniffer.Sniffer
import net.minecraft.world.entity.animal.squid.GlowSquid
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.*
import net.minecraft.world.entity.monster.creaking.Creaking
import net.minecraft.world.entity.monster.spider.CaveSpider
import net.minecraft.world.entity.monster.warden.Warden
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.entity.BeehiveBlockEntity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.SafariAPI
import net.wapic.wpcmod.util.SafariAPI.Zone
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext

object SafariESP : EspFeature() {

	private val config get() = WpcMod.config.hunting.safari

	// TODO: Better hive ESP & fix DisplayEntities remaining after being captured
	fun init() {
		WorldRenderEvent.EVENT.register(::onRenderWorld)
	}

	private fun isEntityInZone(entity: Entity, zone: Zone): Boolean {
		return zone.box.isInside(entity.blockX, entity.blockY, entity.blockZ)
	}

	private fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		val beeHives = Utils.getLoadedBlockEntities().filterIsInstance<BeehiveBlockEntity>()
		for (hive in beeHives) {
			worldRenderContext.drawText(
				Component.nullToEmpty(hive.occupantCount.toString()).visualOrderText,
				hive.blockPos.center,
				2f,
				false
			)
			worldRenderContext.drawBoundingBox(hive.blockPos.center, 1f, 1f)
		}
	}

	private fun computeIcyMobs(entity: Entity) = when (entity) {
		is SnowGolem, is PolarBear, is GlowSquid,
		is Goat, is Dolphin, is Ravager -> true
		is TropicalFish -> entity.pattern == TropicalFish.Pattern.SNOOPER

		// Troodon & Mantis Shrimp
		is Display.ItemDisplay -> {
			val isIcyEntity = isEntityInZone(entity, Zone.ICY) && entity.posRotInterpolationDuration == 3
			isIcyEntity && entity.itemStack.item == Items.PLAYER_HEAD
		}

		else -> false
	}

	private fun computeHauntedMobs(entity: Entity): Boolean = when (entity) {
		is CaveSpider, is Bat, is Phantom, is Warden -> true
		is Endermite -> entity.y > 60
		is Shulker -> entity.color == DyeColor.PURPLE
		is RemotePlayer -> entity.name.string == "Hideyho "
		is ArmorStand -> entity.headTexture == HeadTextures.GAZER

		// Duplico, Gimmiegold, Hideonwall(Moving)
		is Display.ItemDisplay -> isEntityInZone(entity, Zone.HAUNTED) && entity.posRotInterpolationDuration == 3
		else -> false
	}

	private fun computeCavernMobs(entity: Entity): Boolean = when (entity) {
		is Armadillo, is Vex, is Sniffer -> true
		is TropicalFish -> entity.pattern == TropicalFish.Pattern.CLAYFISH
		is Silverfish -> !entity.isInvisible

		// Chuckwalla & Flitter
		is Display.ItemDisplay -> {
			val isCavernEntity = isEntityInZone(entity, Zone.CAVERN) && entity.posRotInterpolationDuration == 3
			isCavernEntity && entity.itemStack.item == Items.PLAYER_HEAD
		}

		is ArmorStand -> {
			if (entity.headTexture == HeadTextures.DRIFTLING) return entity.scale == 1.25f
			if (entity.headTexture == HeadTextures.SHYWORM_HEAD) return true
			return false
		}

		else -> false
	}

	private fun computeForestMobs(entity: Entity) = when (entity) {
		is Fox, is Bee, is Parrot, is Frog, is Creaking, is Panda -> true
		is Shulker -> entity.color == DyeColor.GREEN
		is Display.ItemDisplay -> entity.posRotInterpolationDuration == 3 && entity.itemStack.item == Items.GREEN_SHULKER_BOX
		else -> false
	}

	override fun compute(entity: Entity): GlowableESPConfig? {
		val shouldHaveESP = when (Zone.fromBlockPos(MC.player?.blockPosition())) {
			Zone.ICY -> computeIcyMobs(entity)
			Zone.HAUNTED -> computeHauntedMobs(entity)
			Zone.CAVERN -> computeCavernMobs(entity)
			Zone.FOREST -> computeForestMobs(entity)
			Zone.NONE -> false
		}

		return if (shouldHaveESP) config.critter else null
	}

	override fun isEnabled() = SafariAPI.inSafari
}