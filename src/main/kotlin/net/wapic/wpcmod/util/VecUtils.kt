package net.wapic.wpcmod.util

import net.minecraft.util.math.Vec3d

object VecUtils {
	operator fun Vec3d.unaryMinus(): Vec3d = Vec3d(-x, -y, -z)
}