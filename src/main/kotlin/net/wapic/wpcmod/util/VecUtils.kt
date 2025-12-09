package net.wapic.wpcmod.util

import net.minecraft.world.phys.Vec3

object VecUtils {
	operator fun Vec3.unaryMinus(): Vec3 = Vec3(-x, -y, -z)
}