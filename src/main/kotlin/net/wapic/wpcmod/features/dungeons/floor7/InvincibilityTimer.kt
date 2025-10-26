package net.wapic.wpcmod.features.dungeons.floor7

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.text.Text
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.ServerTickEvent
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.hud.SimpleHudElement
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.Utils.toFixed
import net.wapic.wpcmod.util.render.drawText

object InvincibilityTimer : SimpleHudElement("Invincibility Timer", 80, 33) {

	private val config get() = WpcMod.config.dungeon.invincibilityTimer

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		ServerTickEvent.EVENT.register { InvincibilityType.entries.forEach(InvincibilityType::tick) }
		WorldChangeEvent.AFTER.register { InvincibilityType.entries.forEach(InvincibilityType::reset) }
	}

	fun onMessageReceived(text: Text, actionBar: Boolean) {
		if (!isActive || actionBar) return
		InvincibilityType.entries.firstOrNull { it.regex.matches(text.string) }?.proc()
	}

	override fun render(drawContext: DrawContext, renderTickCounter: RenderTickCounter) {
		if (!isActive) return
		val matrixStack = drawContext.matrices
		matrixStack.push()
		applyTransformations(matrixStack)

		InvincibilityType.entries.filter { it.currentCooldown > 0 || it.activeTime > 0 }.forEachIndexed { index, type ->
			val time =
				if (type.activeTime > 0) "Immunity: ${(type.activeTime / 20f).toFixed()}" else "Cooldown: ${(type.currentCooldown / 20f).toFixed()}"
			drawContext.drawText("§6${type} ${time}§6s", 2, 2 + index * 10, 0xffffff, true)
		}

		matrixStack.pop()
	}

	override fun isActive(): Boolean {
		return DungeonUtils.inDungeons && isEnabled
	}

	override fun isEnabled(): Boolean {
		return config.hud
	}

	private enum class InvincibilityType(
		val regex: Regex,
		private val maxInvincibilityTicks: Int,
		val maxCooldownTicks: Int,
	) {
		SPIRIT(Regex("^Second Wind Activated! Your Spirit Mask saved your life!$"), 30, 600),
		BONZO(Regex("^Your (?:. )?Bonzo's Mask saved your life!$"), 60, 3600),
		PHOENIX(Regex("^Your Phoenix Pet saved you from certain death!$"), 80, 1200);

		var activeTime: Int = 0
			private set
		var currentCooldown: Int = 0
			private set

		fun proc() {
			if (config.title) ChatUtils.sendAlert(Text.literal("$name Procced"))
			if (config.message) Utils.runCommand("pc $name Procced")
			activeTime = maxInvincibilityTicks
			currentCooldown = maxCooldownTicks
		}

		fun tick() {
			if (currentCooldown > 0) currentCooldown--
			if (activeTime > 0) activeTime--
		}

		fun reset() {
			currentCooldown = 0
			activeTime = 0
		}

		override fun toString(): String {
			return name.lowercase().replaceFirstChar { it.uppercase() }
		}
	}
}