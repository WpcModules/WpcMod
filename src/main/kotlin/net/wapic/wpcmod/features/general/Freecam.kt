package net.wapic.wpcmod.features.general

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.event.player.*
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.Entity
import net.minecraft.util.ActionResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.freecam.CameraEntity
import net.wapic.wpcmod.util.freecam.CameraEntity.Companion.createCameraEntity

class Freecam {
	private val bind: KeyBinding =
		KeyBindingHelper.registerKeyBinding(KeyBinding("Freecam", InputUtil.GLFW_KEY_B, "WpcMod"))
	private var cameraMotion: Vec3d = Vec3d.ZERO

	init {
		UseItemCallback.EVENT.register { _, _, _ -> onInteract() }
		UseBlockCallback.EVENT.register { _, _, _, _ -> onInteract() }
		UseEntityCallback.EVENT.register { _, _, _, _, _ -> onInteract() }
		AttackEntityCallback.EVENT.register { _, _, _, _, _ -> onInteract() }
		AttackBlockCallback.EVENT.register { _, _, _, _, _ -> onInteract() }
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		WorldChangeEvent.EVENT.register { client, _ ->
			removeCamera(client)
			isEnabled = false
		}
	}

	fun onInteract(): ActionResult {
		if (isEnabled) {
			return ActionResult.FAIL
		}
		return ActionResult.PASS
	}

	fun getRampedMotion(current: Double, input: Int, rampAmount: Double, decelerationFactor: Double): Double {
		var r = rampAmount
		var c = current

		if (input != 0) {
			if (input < 0) {
				r *= -1.0
			}

			if ((input < 0) != (current < 0.0)) {
				c = 0.0
			}

			c = MathHelper.clamp(c + r, -1.0, 1.0)
		} else {
			c *= decelerationFactor
		}

		return c
	}

	fun calculateMotionWithDeceleration(lastMotion: Vec3d, rampAmount: Double, decelerationFactor: Double): Vec3d {
		val options = MinecraftClient.getInstance().options
		var forward = 0
		var vertical = 0
		var strafe = 0

		if (options.forwardKey.isPressed) forward += 1
		if (options.backKey.isPressed) forward -= 1
		if (options.leftKey.isPressed) strafe += 1
		if (options.rightKey.isPressed) strafe -= 1
		if (options.jumpKey.isPressed) vertical += 1
		if (options.sneakKey.isPressed) vertical -= 1

		val speed = if (forward != 0 && strafe != 0) 1.2 else 1.0
		val forwardRamped = getRampedMotion(lastMotion.x, forward, rampAmount, decelerationFactor) / speed
		val verticalRamped = getRampedMotion(lastMotion.y, vertical, rampAmount, decelerationFactor)
		val strafeRamped = getRampedMotion(lastMotion.z, strafe, rampAmount, decelerationFactor) / speed

		return Vec3d(forwardRamped, verticalRamped, strafeRamped)
	}

	fun onTick(client: MinecraftClient) {
		if (bind.wasPressed()) {
			toggle(client)
		}

		camera?.let {
			it.updateLastTickPosition()
			val cameraMotion = calculateMotionWithDeceleration(cameraMotion, 0.15, 0.4)
			it.handleMotion(cameraMotion.x, cameraMotion.y, cameraMotion.z)
		}
	}

	companion object {
		var isEnabled: Boolean = false
			private set
		var originalCameraEntity: Entity? = null
			private set
		var originalCameraWasPlayer: Boolean = false
			private set
		var camera: CameraEntity? = null
			private set

		fun toggle(client: MinecraftClient) {
			isEnabled = !isEnabled
			if (isEnabled) {
				createAndSetCamera(client)
				ChatUtils.sendMessage("Freecam Enabled")
			} else {
				removeCamera(client)
				ChatUtils.sendMessage("Freecam Disabled")
			}
		}

		fun createAndSetCamera(mc: MinecraftClient) {
			camera = createCameraEntity(mc)
			camera?.let {
				originalCameraEntity = mc.cameraEntity
				originalCameraWasPlayer = originalCameraEntity == mc.player

				mc.cameraEntity = it
				mc.chunkCullingEnabled = false
			}
		}

		fun removeCamera(mc: MinecraftClient) {
			mc.world?.let {
				mc.cameraEntity = if (originalCameraWasPlayer) mc.player else originalCameraEntity
				mc.chunkCullingEnabled = true
			}

			originalCameraEntity = null
			camera = null
		}

		fun updateCameraRotations(yawChange: Float, pitchChange: Float) {
			camera?.updateCameraRotations(yawChange, pitchChange)
		}
	}
}