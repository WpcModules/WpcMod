package net.wapic.wpcmod.util.freecam

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.recipebook.ClientRecipeBook
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.MovementType
import net.minecraft.stat.StatHandler
import net.minecraft.util.PlayerInput
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.wapic.wpcmod.features.general.Freecam
import kotlin.math.cos
import kotlin.math.sin

class CameraEntity(
	mc: MinecraftClient,
	world: ClientWorld,
	netHandler: ClientPlayNetworkHandler,
	statHandler: StatHandler,
	recipeBook: ClientRecipeBook
) : ClientPlayerEntity(mc, world, netHandler, statHandler, recipeBook, PlayerInput.DEFAULT, false) {

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
		val yaw = this.yaw + yaw * 0.15F
		val pitch = MathHelper.clamp(this.pitch + pitch * 0.15F, -90F, 90F)

		this.yaw = yaw
		this.pitch = pitch
		this.setCameraRotations(yaw, pitch)
	}

	fun setCameraRotations(yaw: Float, pitch: Float) {
		this.yaw = yaw
		this.pitch = pitch
		this.headYaw = yaw
	}

	fun updateLastTickPosition() {
		this.lastRenderX = this.x
		this.lastRenderY = this.y
		this.lastRenderZ = this.z

		this.lastX = this.x
		this.lastY = this.y
		this.lastZ = this.z

		this.lastYaw = this.yaw
		this.lastPitch = this.pitch

		this.lastHeadYaw = this.headYaw
	}

	fun handleMotion(forward: Double, up: Double, strafe: Double) {
		val yaw = this.yaw
		val scale = (movementSpeed * 40)
		val xFactor = sin(yaw * Math.PI / 180)
		val zFactor = cos(yaw * Math.PI / 180)

		val x = (strafe * zFactor - forward * xFactor) * scale
		val y = up * scale
		val z = (forward * zFactor + strafe * xFactor) * scale

		this.velocity = Vec3d(x, y, z)
		this.move(MovementType.SELF, this.velocity)
	}

	companion object {
		fun createCameraEntity(mc: MinecraftClient): CameraEntity? {
			mc.player?.let {
				if (it.isOnGround) {
					it.velocity = Vec3d.ZERO
				}

				val cam = CameraEntity(mc, mc.world!!, it.networkHandler, it.statHandler, it.recipeBook)
				cam.noClip = true
				cam.copyPositionAndRotation(it)
				cam.velocity = Vec3d.ZERO

				return cam
			}

			return null
		}
	}
}