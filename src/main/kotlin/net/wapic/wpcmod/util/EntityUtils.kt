package net.wapic.wpcmod.util

import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.predicate.entity.EntityPredicates
import net.wapic.wpcmod.util.ItemUtils.headTexture

object EntityUtils {

	val LivingEntity?.skyBlockMaxHealth: Float
		get() = this?.getAttributeBaseValue(EntityAttributes.MAX_HEALTH)?.toFloat() ?: 0f

	val ArmorStandEntity.headTexture: String? get() = this.getEquippedStack(EquipmentSlot.HEAD).headTexture

	fun getArmorStandsByEntity(entity: Entity): List<ArmorStandEntity> {
		return entity.world.getEntitiesByClass(
			ArmorStandEntity::class.java,
			entity.boundingBox.expand(0.0, 2.0, 0.0),
			EntityPredicates.NOT_MOUNTED
		)
	}

}