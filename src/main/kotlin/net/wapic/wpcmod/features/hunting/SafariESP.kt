package net.wapic.wpcmod.features.hunting

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.RemotePlayer
import net.minecraft.core.BlockPos
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
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.phys.AABB
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.events.BlockEvents
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.mixin.accessors.DisplayAccessor
import net.wapic.wpcmod.util.EntityUtils.biome
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.SafariAPI
import net.wapic.wpcmod.util.SafariAPI.SafariBiome
import net.wapic.wpcmod.util.SafariAPI.SafariBiome.Companion.isOf
import net.wapic.wpcmod.util.SafariAPI.SafariBiome.Companion.isSimilarTo
import net.wapic.wpcmod.util.render.WorldRenderContext

object SafariESP : EspFeature() {

	private val config get() = WpcMod.config.hunting.safari
	private val clickableBeehives: MutableSet<BlockPos> = mutableSetOf()

	fun init() {
		ClientChunkEvents.CHUNK_LOAD.register(::onChunkLoad)
		WorldRenderEvent.EVENT.register(::onRenderWorld)
		BlockEvents.CHANGE.register(::onBlockChange)

		WorldChangeEvent.BEFORE.register {
			clickableBeehives.clear()
		}
	}

	private fun onBlockChange(pos: BlockPos, oldState: BlockState?, newState: BlockState) {
		if (!isEnabled()) return
		val immutablePos = pos.immutable()

		if (!isBeeNest(oldState) && isBeeNest(newState))
			clickableBeehives.add(immutablePos) else clickableBeehives.remove(immutablePos)
	}

	private fun onChunkLoad(level: ClientLevel, chunk: LevelChunk) {
		if (!isEnabled()) return
		chunk.findBlocks(::isBeeNest) { pos, _ -> clickableBeehives.add(pos.immutable()) }
	}

	private fun isBeeNest(blockState: BlockState?) = blockState?.block == Blocks.BEE_NEST

	private fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		if (!isEnabled()) return
		if (!config.honeybugNestESP.box && !config.honeybugNestESP.tracer) return
		if (MC.player?.biome?.isOf(SafariBiome.FOREST) == false) return

		for (hive in clickableBeehives) {
			if (config.honeybugNestESP.box) worldRenderContext.drawBoundingBox(AABB(hive), config.honeybugNestESP.color)
			if (config.honeybugNestESP.tracer) worldRenderContext.drawTracer(hive.center, config.honeybugNestESP.color)
		}
	}

	private fun computeIcyMobs(entity: Entity) = when (entity) {
		is SnowGolem, is PolarBear, is GlowSquid,
		is Goat, is Dolphin, is Ravager -> true
		is TropicalFish -> entity.pattern == TropicalFish.Pattern.SNOOPER

		// Troodon & Mantis Shrimp
		is Display.ItemDisplay -> entity.itemStack.item == Items.PLAYER_HEAD

		else -> false
	}

	private fun computeHauntedMobs(entity: Entity): Boolean = when (entity) {
		is CaveSpider, is Bat, is Phantom, is Warden -> true
		is Endermite -> config.critter.showOutOfBoundsLitterbug || entity.y > 60
		is Shulker -> entity.color == DyeColor.PURPLE
		is RemotePlayer -> entity.name.string == "Hideyho " // Yes, the space is supposed to be there
		is ArmorStand -> entity.headTexture == HeadTextures.GAZER

		// Duplico, Gimmiegold, Hideonwall(Moving)
		is Display.ItemDisplay -> entity.posRotInterpolationDuration == 3
		else -> false
	}

	private fun computeCavernMobs(entity: Entity): Boolean = when (entity) {
		is Armadillo, is Vex, is Sniffer -> true
		is TropicalFish -> entity.pattern == TropicalFish.Pattern.CLAYFISH
		is Silverfish -> !entity.isInvisible

		// Chuckwalla, Flitter, Rockmite mound
		is Display.ItemDisplay -> entity.itemStack.item == Items.PLAYER_HEAD

		is ArmorStand -> {
			if (entity.headTexture == HeadTextures.DRIFTLING) return entity.scale == 1.25f
			return entity.headTexture == HeadTextures.SHYWORM_HEAD
		}

		else -> false
	}

	private fun computeForestMobs(entity: Entity) = when (entity) {
		is Fox, is Bee, is Parrot, is Frog, is Creaking, is Panda -> true
		is Shulker -> entity.color == DyeColor.GREEN
		is Display.ItemDisplay -> entity.itemStack.item == Items.GREEN_SHULKER_BOX
		else -> false
	}

	override fun compute(entity: Entity): GlowableESPConfig? {
		val player = MC.player ?: return null
		if (!entity.biome.isSimilarTo(player.biome)) return null
		val entityBiome = SafariBiome.fromBiome(entity.biome) ?: return null

		if (entity is Display.ItemDisplay) { // Hypixel doesn't delete their DisplayEntities they just set their scale to 0
			val scale = entity.entityData.get((entity as? DisplayAccessor)?.dataScale ?: return null)
			if (scale.length() == 0f) return null
		}

		val hasEsp = when (entityBiome) {
			SafariBiome.ICY, SafariBiome.ICY_CAVES -> computeIcyMobs(entity)
			SafariBiome.HAUNTED -> computeHauntedMobs(entity)
			SafariBiome.FOREST -> computeForestMobs(entity)
			SafariBiome.CAVERN -> computeCavernMobs(entity)
		}

		return config.critter.takeIf { hasEsp }
	}

	override fun isEnabled() = SafariAPI.inSafari
}