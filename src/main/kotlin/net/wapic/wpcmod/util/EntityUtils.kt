package net.wapic.wpcmod.util

import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.phys.Vec3

val LivingEntity?.skyBlockMaxHealth: Float
	get() = this?.getAttributeBaseValue(Attributes.MAX_HEALTH)?.toFloat() ?: 0f

	val LivingEntity.headTexture: String get() = this.getItemBySlot(EquipmentSlot.HEAD).headTexture
	val Entity.biome: Holder<Biome> get() = this.level().getBiome(this.blockPosition())

	fun Entity.getRenderPos(partialTick: Float): Vec3 =
		this.getPosition(partialTick).relative(Direction.UP, this.bbHeight / 2.0)

	inline val Entity.lerpedRenderPos: Vec3
		get() = this.getPosition(MC.instance.deltaTracker.getGameTimeDeltaPartialTick(true)).add(.0, this.bbHeight / 2.0, .0)

	inline val Entity.lerpedPos: Vec3
		get() = this.getPosition(MC.instance.deltaTracker.getGameTimeDeltaPartialTick(true))

	inline val Entity.lerpedEyePos: Vec3
		get() = this.getEyePosition(MC.instance.deltaTracker.getGameTimeDeltaPartialTick(true))

	fun Entity.getNearbyArmorStands(): List<ArmorStand> {
		return this.level().getEntitiesOfClass(
			ArmorStand::class.java,
			this.boundingBox.inflate(0.0, 1.0, 0.0),
			EntitySelector.ENTITY_NOT_BEING_RIDDEN
		)
	}
}