package net.wapic.wpcmod.util

import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3

operator fun Vec3.unaryMinus(): Vec3 = Vec3(-x, -y, -z)
operator fun Vec3.minus(vec: Vec3): Vec3 = Vec3(x - vec.x, y - vec.y, z - vec.z)
fun Vec3.up(value: Double) = this.relative(Direction.UP, value)