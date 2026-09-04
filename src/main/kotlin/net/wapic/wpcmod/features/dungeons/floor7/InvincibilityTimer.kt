package net.wapic.wpcmod.features.dungeons.floor7

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.CommonColors
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.ServerTickEvent
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.hud.Mutable
import net.wapic.wpcmod.hud.SimpleHudElement
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.Utils.toFixed
import net.wapic.wpcmod.util.dungeons.DungeonUtils
import net.wapic.wpcmod.util.render.gui.drawTexture

object InvincibilityTimer : SimpleHudElement("Invincibility Timer", 48, 45), Mutable {

	private val config get() = WpcMod.config.dungeon.invincibilityTimer
	override val isEnabled: Boolean get() = config.enabled
	override val isActive: Boolean get() = DungeonUtils.inDungeons && isEnabled && config.hud

	fun init() {
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
		ServerTickEvent.EVENT.register { config.enabledItems.get().forEach(::tick) }
		WorldChangeEvent.AFTER.register { config.enabledItems.get().forEach(::reset) }
		config.enabledItems.addObserver(::onUpdateEnabledItems)
	}

	fun onUpdateEnabledItems(old: MutableList<InvincibilityType>, new: MutableList<InvincibilityType>) {
		this.height = new.size * 15
	}

	override fun notifyObserver() = config.enabledItems.notifyObservers()

	fun onMessageReceived(text: Component, actionBar: Boolean) {
		if (!isActive || actionBar) return
		val type = config.enabledItems.get().firstOrNull { it.regex.matches(text.string) } ?: return
		proc(type)
	}

	override fun render(drawContext: GuiGraphicsExtractor, deltaTicks: Float) {
		if (!isActive) return
		val matrixStack = drawContext.pose()
		matrixStack.pushMatrix()
		applyTransformations(matrixStack)

		config.enabledItems.get().forEachIndexed { index, type ->
			drawContext.drawTexture(type.icon, -1, -1 + index * 15, 0f, 0f, 16, 16, 16, 16)
			val time = when {
				type.currentCooldown <= 0 -> "§aREADY"
				type.activeTime > 0 -> "§6${(type.activeTime / 20f).toFixed(1)}s"
				else -> "§c${(type.currentCooldown / 20f).toFixed(1)}s"
			}
			drawContext.text(MC.font, time, 15, 4 + index * 15, CommonColors.WHITE, true)
		}

		matrixStack.popMatrix()
	}

	fun tick(type: InvincibilityType) {
		if (!isEnabled) return
		if (type.currentCooldown > 0) type.currentCooldown--
		if (type.activeTime > 0) type.activeTime--
	}

	fun proc(type: InvincibilityType) {
		if (!isActive) return
		if (config.title) ChatUtils.sendAlert(Component.literal("$type procced"))
		if (config.message) Utils.runCommand("pc $type procced")
		type.currentCooldown = type.maxCooldownTicks
		type.activeTime = type.maxInvincibilityTicks
	}

	fun reset(type: InvincibilityType) {
		type.currentCooldown = 0
		type.activeTime = 0
	}

	enum class InvincibilityType(val regex: Regex, val maxInvincibilityTicks: Int, val maxCooldownTicks: Int, val icon: Identifier) {
		SPIRIT_MASK(Regex("^Second Wind Activated! Your Spirit Mask saved your life!$"), 60, 600, WpcMod.Identifier("dungeon/immunity/spirit_mask.png")),
		BONZO_MASK(Regex("^Your (?:. )?Bonzo's Mask saved your life!$"), 60, 3600, WpcMod.Identifier("dungeon/immunity/bonzo_mask.png")),
		PHOENIX_PET(Regex("^Your Phoenix Pet saved you from certain death!$"), 40, 1200, WpcMod.Identifier("dungeon/immunity/phoenix_pet.png"));

		var activeTime: Int = 0
		var currentCooldown: Int = 0

		override fun toString(): String = name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }
	}
}