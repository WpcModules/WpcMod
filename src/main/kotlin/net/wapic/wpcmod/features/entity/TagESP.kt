package net.wapic.wpcmod.features.entity

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.Entity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.EntityUtils.getArmorStandsByEntity
import net.wapic.wpcmod.util.render.RenderUtils
import java.util.Locale

object TagESP : MobGlowCache() {

	private val config get() = WpcMod.config.general.esp.tag
	val taggedEntities = mutableSetOf<String>()

	fun init() {
		WorldRenderEvents.END.register(::onRenderWorld)
	}

	private fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		for (entity in worldRenderContext.world().entities) {
			if(entity.name.string.lowercase(Locale.ENGLISH) !in taggedEntities || !isTagged(entity)) continue

			if (config.box)
				RenderUtils.drawBoundingBox(worldRenderContext, entity.boundingBox, config.color.getEffectiveColour())
			if (config.tracer)
				RenderUtils.drawTracer(worldRenderContext, entity.eyePos, config.color.getEffectiveColour())
		}
	}

	fun modifyTagList(context: CommandContext<FabricClientCommandSource>) {
		val player = getString(context, "player").lowercase(Locale.ENGLISH)
		if (taggedEntities.contains(player)) {
			taggedEntities.remove(player)
			ChatUtils.sendMessage("$player is no longer tagged")
		} else {
			taggedEntities.add(player)
			ChatUtils.sendMessage("$player is now tagged")
		}
	}

	fun isTagged(entity: Entity): Boolean {
		val armorStands = getArmorStandsByEntity(entity)
		return armorStands.isNotEmpty() && armorStands.first().name?.string?.lowercase(Locale.ENGLISH) in taggedEntities
	}

	override fun compute(entity: Entity): Int {
		return when {
			config.glow && (isTagged(entity) || entity.name.string.lowercase(Locale.ENGLISH) in taggedEntities) -> config.color.getEffectiveColourRGB()
			else -> MobGlow.NO_GLOW
		}
	}

	override fun isEnabled(): Boolean {
		return taggedEntities.isNotEmpty()
	}
}