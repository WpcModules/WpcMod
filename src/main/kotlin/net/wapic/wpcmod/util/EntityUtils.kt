package net.wapic.wpcmod.util

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.EntitySelector
import net.wapic.wpcmod.util.ItemUtils.headTexture

object EntityUtils {

	val LivingEntity.headTexture: String get() = this.getItemBySlot(EquipmentSlot.HEAD).headTexture

	fun getArmorStandsByEntity(entity: Entity): List<ArmorStand> {
		return entity.level().getEntitiesOfClass(
			ArmorStand::class.java,
			entity.boundingBox.inflate(0.0, 1.0, 0.0),
			EntitySelector.ENTITY_NOT_BEING_RIDDEN
		)
	}
}