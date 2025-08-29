package net.wapic.wpcmod.util

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes

object EntityUtils {

	val LivingEntity?.skyBlockMaxHealth: Float
		get() = this?.getAttributeBaseValue(EntityAttributes.MAX_HEALTH)?.toFloat() ?: 0f


}