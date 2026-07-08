package net.wapic.wpcmod.features.events.diana

import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.events.EntityEvents
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.MC
import java.util.*

object LootshareHelper : EspFeature() {

	private val config get() = WpcMod.config.events.diana.lootshareHelper
	private val taggedMobs = hashSetOf<UUID>()
	private val shouldRender get() = config.box || config.tracer || config.glow

	fun init() {
		AttackEntityCallback.EVENT.register(::onAttackEntity)
		EntityEvents.DEATH.register { taggedMobs.remove(it.uuid) }
	}

	fun onAttackEntity(
		player: Player,
		level: Level,
		hand: InteractionHand,
		entity: Entity,
		hitResult: EntityHitResult?
	): InteractionResult {
		if (!config.enabled || taggedMobs.contains(entity.uuid)) return InteractionResult.PASS

		val addedEntity = taggedMobs.add(entity.uuid)
		if (addedEntity) {
			if (config.soundOnHit) MC.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1f)
			if (config.titleOnHit) ChatUtils.sendAlert(Component.literal("Mob is hit!"))
		}
		return InteractionResult.PASS
	}

	override fun compute(entity: Entity): GlowableESPConfig? = config.takeIf { taggedMobs.contains(entity.uuid) }
	override fun isEnabled(): Boolean = /* Utils.getLocation() == Island.HUB && */
		taggedMobs.isNotEmpty() && shouldRender
}