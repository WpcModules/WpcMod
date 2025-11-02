package net.wapic.wpcmod.features.entity

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.entity.Entity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.EntityUtils.getArmorStandsByEntity
import net.wapic.wpcmod.util.render.WorldRenderContext
import java.util.*

object TagESP : MobGlowCache() {

	private val config get() = WpcMod.config.general.esp.tag
	private val taggedEntities = hashSetOf<String>()

	fun init() {
		WorldRenderEvent.EVENT.register(::onRenderWorld)
	}

	private fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		if(!isEnabled()) return
		val profiler = worldRenderContext.profiler()
		profiler.push("tag-esp")
		for (entity in worldRenderContext.world().entities) {
			if (!isTagged(entity)) continue

			if (config.box)
				worldRenderContext.drawBoundingBox(entity.boundingBox, config.color)
			if (config.tracer)
				worldRenderContext.drawTracer(entity.boundingBox.center, config.color)
		}
		profiler.pop()
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

	fun clearTagList() {
		taggedEntities.clear()
	}

	fun getTagList(): String {
		return taggedEntities.joinToString { it }
	}

	fun isTagged(entity: Entity): Boolean {
		if (entity.name.string.lowercase(Locale.ENGLISH) in taggedEntities) return true

		val armorStands = getArmorStandsByEntity(entity)
		if (armorStands.isEmpty()) return false

		return taggedEntities.any { armorStands.first().name.string.lowercase(Locale.ENGLISH).contains(it) }
	}

	override fun compute(entity: Entity): ChromaColour? {
		return when {
			config.glow && isTagged(entity) -> config.color
			else -> null
		}
	}

	override fun isEnabled(): Boolean {
		return taggedEntities.isNotEmpty()
	}
}