package net.wapic.wpcmod.features.general

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.fabricmc.fabric.api.event.player.*
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.util.Mth
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.freecam.CameraEntity
import net.wapic.wpcmod.util.freecam.CameraEntity.Companion.createCameraEntity

class Freecam {
	private val bind: KeyMapping =
		KeyMappingHelper.registerKeyMapping(KeyMapping("freecam", InputConstants.KEY_B, WpcMod.category))
	private var cameraMotion: Vec3 = Vec3.ZERO

	init {
		UseItemCallback.EVENT.register { _, _, _ -> onInteract() }
		UseBlockCallback.EVENT.register { _, _, _, _ -> onInteract() }
		UseEntityCallback.EVENT.register { _, _, _, _, _ -> onInteract() }
		AttackEntityCallback.EVENT.register { _, _, _, _, _ -> onInteract() }
		AttackBlockCallback.EVENT.register { _, _, _, _, _ -> onInteract() }
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		WorldChangeEvent.BEFORE.register { _ ->
			removeCamera(MC.instance)
			isEnabled = false
		}
	}

	fun onInteract(): InteractionResult = if (isEnabled) InteractionResult.FAIL else InteractionResult.PASS

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

			c = Mth.clamp(c + r, -1.0, 1.0)
		} else {
			c *= decelerationFactor
		}

		return c
	}

	fun calculateMotionWithDeceleration(lastMotion: Vec3, rampAmount: Double, decelerationFactor: Double): Vec3 {
		val options = MC.options
		var forward = 0
		var vertical = 0
		var strafe = 0

		if (options.keyUp.isDown) forward += 1
		if (options.keyDown.isDown) forward -= 1
		if (options.keyLeft.isDown) strafe += 1
		if (options.keyRight.isDown) strafe -= 1
		if (options.keyJump.isDown) vertical += 1
		if (options.keyShift.isDown) vertical -= 1

		val speed = if (forward != 0 && strafe != 0) 1.2 else 1.0
		val forwardRamped = getRampedMotion(lastMotion.x, forward, rampAmount, decelerationFactor) / speed
		val verticalRamped = getRampedMotion(lastMotion.y, vertical, rampAmount, decelerationFactor)
		val strafeRamped = getRampedMotion(lastMotion.z, strafe, rampAmount, decelerationFactor) / speed

		return Vec3(forwardRamped, verticalRamped, strafeRamped)
	}

	fun onTick(client: Minecraft) {
		if (bind.consumeClick()) {
			toggle(client)
		}

		camera?.let {
			it.updateLastTickPosition()
			val cameraMotion = calculateMotionWithDeceleration(cameraMotion, 0.15, 0.4)
			val forward = if (client.options.keySprint.isDown) cameraMotion.x * 2 else cameraMotion.x
			it.handleMotion(forward, cameraMotion.y, cameraMotion.z)
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

		fun toggle(client: Minecraft) {
			isEnabled = !isEnabled
			if (isEnabled) {
				createAndSetCamera(client)
				ChatUtils.sendMessage("Freecam Enabled")
			} else {
				removeCamera(client)
				ChatUtils.sendMessage("Freecam Disabled")
			}
		}

		fun createAndSetCamera(mc: Minecraft) {
			camera = createCameraEntity(mc)
			camera?.let {
				originalCameraEntity = mc.cameraEntity
				originalCameraWasPlayer = originalCameraEntity == mc.player

				mc.cameraEntity = it
				mc.smartCull = false
			}
		}

		fun removeCamera(mc: Minecraft) {
			mc.level?.let {
				mc.cameraEntity = if (originalCameraWasPlayer) mc.player else originalCameraEntity
				mc.smartCull = true
			}

			originalCameraEntity = null
			camera = null
		}

		fun updateCameraRotations(yawChange: Float, pitchChange: Float) {
			camera?.updateCameraRotations(yawChange, pitchChange)
		}
	}
}