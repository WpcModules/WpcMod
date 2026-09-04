package net.wapic.wpcmod.features.entity

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.getNearbyArmorStands
import net.wapic.wpcmod.util.render.state.EntityState
import java.util.*

object TagESP : EspFeature() {

	private val config get() = WpcMod.config.general.esp.tag
	private val tagList = hashSetOf<String>()

	fun init() = Unit

	fun modifyTagList(context: CommandContext<FabricClientCommandSource>) {
		val entityName = getString(context, "entityName").lowercase(Locale.ENGLISH)
		if (entityName == "clear") return clearTagList()

		if (tagList.contains(entityName)) {
			tagList.remove(entityName)
			ChatUtils.sendMessage("$entityName is no longer tagged")
		} else {
			tagList.add(entityName)
			ChatUtils.sendMessage("$entityName is now tagged")
		}
	}

	fun clearTagList() {
		val amountRemoved = tagList.count()
		tagList.clear()
		ChatUtils.sendMessage("Removed $amountRemoved item${if (amountRemoved == 1) "" else "s"} from the tag list")
	}

	fun getTagList(): String = tagList.joinToString()

	private fun isTagged(entity: Entity): Boolean {
		val displayName = entity.name.string.lowercase(Locale.ENGLISH)
		val plainTextName = entity.plainTextName.lowercase(Locale.ENGLISH)

		if (displayName in tagList || plainTextName in tagList) return true
		if (entity !is ArmorStand) {
			return entity.getNearbyArmorStands().firstOrNull { e ->
				tagList.any {
					e.name.string.contains(
						it,
						true
					)
				}
			} != null
		}

		return false
	}

	override fun compute(entity: Entity): EntityState? {
		return if (isTagged(entity)) EntityState(config) else null
	}

	override fun isEnabled(): Boolean = tagList.isNotEmpty() && (config.box || config.tracer || config.glow)
}