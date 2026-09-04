package net.wapic.wpcmod.features.hunting

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.RemotePlayer
import net.minecraft.core.BlockPos
import net.minecraft.util.profiling.ProfilerFiller
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
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.BlockEvents
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.mixin.accessors.DisplayAccessor
import net.wapic.wpcmod.util.*
import net.wapic.wpcmod.util.SafariAPI.SafariBiome
import net.wapic.wpcmod.util.SafariAPI.SafariBiome.Companion.isOf
import net.wapic.wpcmod.util.SafariAPI.SafariBiome.Companion.isSimilarTo
import net.wapic.wpcmod.util.render.WpcModExtractionContext
import net.wapic.wpcmod.util.render.state.EntityState

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

	private fun onRenderWorld(context: WpcModExtractionContext, profiler: ProfilerFiller) {
		if (!isEnabled()) return
		if (!config.honeybugNestESP.box && !config.honeybugNestESP.tracer) return
		if (MC.player?.biome?.isOf(SafariBiome.FOREST) == false) return

		for (hive in clickableBeehives) context.blockESP(hive, config.honeybugNestESP)
	}

	private fun computeIcyMobs(entity: Entity): EntityState? = when (entity) {
		is SnowGolem, is PolarBear, is GlowSquid,
		is Goat, is Dolphin, is Ravager -> entity.state()
		is TropicalFish -> entity.state().takeIf { entity.pattern == TropicalFish.Pattern.SNOOPER }

		// Troodon & Mantis Shrimp
		is Display.ItemDisplay -> {
			entity.state(.6f, .6f, -0.5f).takeIf { entity.itemStack.item == Items.PLAYER_HEAD }
		}

		else -> null
	}

	private fun computeHauntedMobs(entity: Entity): EntityState? = when (entity) {
		is CaveSpider, is Bat, is Phantom, is Warden -> entity.state()
		is Endermite -> entity.state().takeIf { config.critter.showOutOfBoundsLitterbug || entity.y > 60 }
		is Shulker -> entity.state().takeIf { entity.color == DyeColor.PURPLE }
		is RemotePlayer -> entity.state().takeIf { entity.name.string == "Hideyho " }
		is ArmorStand -> entity.state().takeIf { entity.headTexture == HeadTextures.GAZER }

		// Duplico, Gimmiegold, Hideonwall(Moving)
		is Display.ItemDisplay -> {
			if (entity.itemStack.item == Items.PLAYER_HEAD) return entity.state(.8f, .8f)
			return entity.state(1f, 1f).takeIf { entity.posRotInterpolationDuration == 3 }
		}

		else -> null
	}

	private fun computeCavernMobs(entity: Entity): EntityState? {
		val state = when (entity) {
			is Armadillo, is Vex, is Sniffer -> entity.state()
			is TropicalFish -> entity.state().takeIf { entity.pattern == TropicalFish.Pattern.CLAYFISH }
			is Silverfish -> entity.state().takeIf { !entity.isInvisible }

			// Chuckwalla, Flitter, Rockmite mound
			is Display.ItemDisplay -> entity.state(.5f, .5f, -.35f)
				.takeIf { entity.itemStack.item == Items.PLAYER_HEAD }

			is ArmorStand -> {
				if (entity.headTexture == HeadTextures.DRIFTLING) return entity.state(.8f, .8f, 1.45f)
					.takeIf { entity.scale == 1.25f }
				entity.state(.8f, .8f, 1.35f).takeIf { entity.headTexture == HeadTextures.SHYWORM_HEAD }
			}

			else -> null
		}
		return state
	}

	private fun computeForestMobs(entity: Entity): EntityState? {
		val state = when (entity) {
			is Fox, is Bee, is Parrot, is Frog, is Creaking, is Panda -> entity.state()
			is Shulker -> entity.state().takeIf { entity.color == DyeColor.GREEN }

			is Display.ItemDisplay -> {
				entity.state(1f, 1f).takeIf { entity.itemStack.item == Items.DYED_SHULKER_BOX.green }
			}

			else -> null
		}
		return state
	}

	override fun compute(entity: Entity): EntityState? {
		val player = MC.player ?: return null
		if (!entity.biome.isSimilarTo(player.biome)) return null
		val entityBiome = SafariBiome.fromBiome(entity.biome) ?: return null

		if (entity is Display.ItemDisplay) { // Hypixel doesn't delete their DisplayEntities they just set their scale to 0
			val scale = entity.entityData.get((entity as? DisplayAccessor)?.dataScale ?: return null)
			if (scale.length() == 0f) return null
		}

		return when (entityBiome) {
			SafariBiome.ICY, SafariBiome.ICY_CAVES -> computeIcyMobs(entity)
			SafariBiome.HAUNTED -> computeHauntedMobs(entity)
			SafariBiome.FOREST -> computeForestMobs(entity)
			SafariBiome.CAVERN -> computeCavernMobs(entity)
		}
	}

	fun Entity.state(width: Float = this.bbWidth, height: Float = this.bbHeight, yOffset: Float = 0f) =
		EntityState(config.critter, width, height, yOffset)

	override fun isEnabled() = SafariAPI.inSafari
}