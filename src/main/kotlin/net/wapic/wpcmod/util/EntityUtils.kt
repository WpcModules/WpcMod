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
import net.wapic.wpcmod.util.ItemUtils.headTexture

object EntityUtils {

	val LivingEntity?.skyBlockMaxHealth: Float
		get() = this?.getAttributeBaseValue(Attributes.MAX_HEALTH)?.toFloat() ?: 0f

	val LivingEntity.headTexture: String get() = this.getItemBySlot(EquipmentSlot.HEAD).headTexture
	val Entity.biome: Holder<Biome> get() = this.level().getBiome(this.blockPosition())

	fun Entity.getRenderPos(partialTick: Float): Vec3 =
		this.getPosition(partialTick).relative(Direction.UP, this.bbHeight / 2.0)

	fun getArmorStandsByEntity(entity: Entity): List<ArmorStand> {
		return entity.level().getEntitiesOfClass(
			ArmorStand::class.java,
			entity.boundingBox.inflate(0.0, 1.0, 0.0),
			EntitySelector.ENTITY_NOT_BEING_RIDDEN
		)
	}

	fun getNearestNonArmorStandEntity(entity: ArmorStand): Entity? {
		return MC.level?.getEntities(entity, entity.boundingBox.inflate(0.0, 2.0, 0.0))
			?.firstOrNull { it !is ArmorStand }
	}
}