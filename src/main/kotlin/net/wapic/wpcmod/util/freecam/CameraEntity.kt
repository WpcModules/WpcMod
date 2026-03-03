package net.wapic.wpcmod.util.freecam

import net.minecraft.client.ClientRecipeBook
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.player.LocalPlayer
import net.minecraft.stats.StatsCounter
import net.minecraft.util.Mth
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.player.Input
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.features.general.Freecam
import kotlin.math.cos
import kotlin.math.sin

class CameraEntity(
	mc: Minecraft,
	world: ClientLevel,
	netHandler: ClientPacketListener,
	statHandler: StatsCounter,
	recipeBook: ClientRecipeBook
) : LocalPlayer(mc, world, netHandler, statHandler, recipeBook, Input.EMPTY, false) {
	var flySpeed = 10f

	override fun isSpectator(): Boolean {
		return true
	}

	override fun getId(): Int {
		Freecam.originalCameraEntity?.let {
			return it.id
		}
		return super.getId()
	}

	fun updateCameraRotations(yaw: Float, pitch: Float) {
		val yaw = this.yRot + yaw * 0.15F
		val pitch = Mth.clamp(this.xRot + pitch * 0.15F, -90F, 90F)

		this.yRot = yaw
		this.xRot = pitch
		this.setCameraRotations(yaw, pitch)
	}

	fun setCameraRotations(yaw: Float, pitch: Float) {
		this.yRot = yaw
		this.xRot = pitch
		this.yHeadRot = yaw
	}

	fun updateLastTickPosition() {
		this.xOld = this.x
		this.yOld = this.y
		this.zOld = this.z

		this.xo = this.x
		this.yo = this.y
		this.zo = this.z

		this.yRotO = this.yRot
		this.xRotO = this.xRot

		this.yHeadRotO = this.yHeadRot
	}

	fun handleMotion(forward: Double, up: Double, strafe: Double) {
		val yaw = this.yRot
		val xFactor = sin(yaw * Math.PI / 180)
		val zFactor = cos(yaw * Math.PI / 180)

		val x = (strafe * zFactor - forward * xFactor) * flySpeed
		val y = up * flySpeed
		val z = (forward * zFactor + strafe * xFactor) * flySpeed

		this.deltaMovement = Vec3(x, y, z)
		this.move(MoverType.SELF, this.deltaMovement)
	}

	companion object {
		fun createCameraEntity(mc: Minecraft): CameraEntity? {
			mc.player?.let {
				if (it.onGround()) {
					it.deltaMovement = Vec3.ZERO
				}

				val cam = CameraEntity(mc, mc.level!!, it.connection, it.stats, it.recipeBook)
				cam.noPhysics = true
				cam.copyPosition(it)
				cam.deltaMovement = Vec3.ZERO

				return cam
			}

			return null
		}
	}
}