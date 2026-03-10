package net.wapic.wpcmod.features.entity

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.core.Direction
import net.minecraft.util.profiling.Profiler
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.phys.Vec3
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.EntityUtils.getArmorStandsByEntity
import net.wapic.wpcmod.util.EntityUtils.getNearestNonArmorStandEntity
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.render.WorldRenderContext
import java.util.*

object TagESP : MobGlowCache() {

	private val config get() = WpcMod.config.general.esp.tag
	private val tagList = hashSetOf<String>()
	private var taggedEntities: Set<Entity> = emptySet()

	private val shouldRender get() = config.box || config.tracer
	private val shouldScan get() = (shouldRender || config.glow) && tagList.isNotEmpty()

	fun init() {
		WorldRenderEvent.EVENT.register(::onRenderWorld)
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		WorldChangeEvent.BEFORE.register { clearCache() }
	}

	private fun onTick(client: Minecraft) {
		if (!shouldScan) return

		val profiler = Profiler.get()
		profiler.push("tagESP")
		taggedEntities = MC.entities?.filter(::isTagged)?.mapNotNull { entity ->
			return@mapNotNull if (entity is ArmorStand) findRelatedEntity(entity) else entity
		}?.toSet() ?: return
		profiler.pop()
	}

	private fun onRenderWorld(worldRenderContext: WorldRenderContext) {
		if (!shouldRender) return

		val profiler = worldRenderContext.profiler
		profiler.push("tagESP")
		val deltaTicks = worldRenderContext.tickCounter.getGameTimeDeltaPartialTick(true)

		for (entity in taggedEntities) {
			val pos = getRenderPos(entity, deltaTicks)
			if (config.box) worldRenderContext.drawBoundingBox(pos, entity.bbWidth, entity.bbHeight, config.color)
			if (config.tracer) worldRenderContext.drawTracer(pos, config.color)
		}
		profiler.pop()
	}

	fun modifyTagList(context: CommandContext<FabricClientCommandSource>) {
		val entityName = getString(context, "entityName").lowercase(Locale.ENGLISH)
		if (entityName == "clear") return clearTagList()

		if (tagList.contains(entityName)) {
			tagList.remove(entityName)
			if (tagList.isEmpty()) clearCache()
			ChatUtils.sendMessage("$entityName is no longer tagged")
		} else {
			tagList.add(entityName)
			ChatUtils.sendMessage("$entityName is now tagged")
		}
	}

	fun clearCache() {
		taggedEntities = emptySet()
	}

	fun clearTagList() {
		val amountRemoved = tagList.count()
		tagList.clear()
		clearCache()
		ChatUtils.sendMessage("Removed $amountRemoved item${if (amountRemoved == 1) "" else "s"} from the tag list")
	}

	fun getTagList(): String = tagList.joinToString()

	private fun isTagged(entity: Entity): Boolean {
		val displayName = entity.name.string.lowercase(Locale.ENGLISH)
		val plainTextName = entity.plainTextName.lowercase(Locale.ENGLISH)

		if (entity is ArmorStand && tagList.any { displayName.contains(it) }) {
			return findRelatedEntity(entity) != null
		}

		return plainTextName in tagList || displayName in tagList
	}

	private fun findRelatedEntity(entity: ArmorStand): Entity? {
		return getNearestNonArmorStandEntity(entity)
			?: getArmorStandsByEntity(entity).find { it.headTexture.isNotEmpty() }
	}

	private fun getRenderPos(entity: Entity, deltaTicks: Float): Vec3 {
		return entity.getPosition(deltaTicks).relative(Direction.UP, entity.bbHeight / 2.0)
	}

	override fun compute(entity: Entity): ChromaColour? = config.color.takeIf { entity in taggedEntities }

	override fun isEnabled(): Boolean = tagList.isNotEmpty() && config.glow
}