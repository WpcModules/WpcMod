package net.wapic.wpcmod.features.dungeons

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.passive.BatEntity
import net.minecraft.entity.player.PlayerEntity
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.entity.MobGlow
import net.wapic.wpcmod.features.entity.MobGlowCache
import net.wapic.wpcmod.util.EntityUtils.getArmorStandsByEntity
import net.wapic.wpcmod.util.EntityUtils.headTexture
import net.wapic.wpcmod.util.HeadTextures
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.RenderUtils

class DungeonESP : MobGlowCache() {

	private val config get() = WpcMod.config.dungeon.esp
	private val miniBosses: List<String> = listOf("Lost Adventurer", "Shadow Assassin", "Diamond Guy")

	init {
		WorldRenderEvents.END.register(::renderWorld)
	}

	private fun renderWorld(worldRenderContext: WorldRenderContext) {
		if(!isEnabled()) return

		for(entity in worldRenderContext.world().entities) {
			val entityConfig = when {
				isStarredMob(entity) || (entity is ArmorStandEntity && entity.headTexture == HeadTextures.FEL) -> config.starMob
				entity is BatEntity -> config.bat
				entity is PlayerEntity && entity.name.string in miniBosses -> config.miniboss
				else -> continue
			}

			if(entityConfig.box) {
				RenderUtils.drawBoundingBox(worldRenderContext, entity.boundingBox, entityConfig.color.getEffectiveColour())
			}
			if(entityConfig.tracer) {
				RenderUtils.drawTracer(worldRenderContext, entity.x, entity.y, entity.z, entityConfig.color.getEffectiveColour())
			}
		}
	}

	fun isStarredMob(entity: Entity): Boolean {
		val armorStands = getArmorStandsByEntity(entity)
		return armorStands.isNotEmpty() && armorStands.first().name?.string?.contains("✯") ?: false
	}

	override fun compute(entity: Entity): Int {
		return when {
			config.starMob.glow && (isStarredMob(entity) || (entity is ArmorStandEntity && entity.headTexture == HeadTextures.FEL)) -> config.starMob.color.getEffectiveColourRGB()
			config.miniboss.glow && entity.name.string in miniBosses -> config.miniboss.color.getEffectiveColourRGB()
			config.bat.glow && entity is BatEntity -> config.bat.color.getEffectiveColourRGB()
			else -> MobGlow.NO_GLOW
		}
	}

	override fun isEnabled(): Boolean = Utils.getLocation() == Island.DUNGEON
}