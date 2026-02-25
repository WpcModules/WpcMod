package net.wapic.wpcmod.features.mining

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.TabListUtil
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.WorldRenderContext
import net.wapic.wpcmod.util.render.toChromaColour

object CorpseESP {

	val config get() = WpcMod.config.mining.esp.corpse
	var corpses: List<Corpse> = emptyList()
	val corpseRegex = Regex("^ (Lapis|Umber|Tungsten): (NOT )?LOOTED$")

	data class Corpse(val entity: ArmorStand, val color: ChromaColour)

	fun init() {
		WorldRenderEvent.EVENT.register(::onRenderWorld)
		WorldChangeEvent.BEFORE.register(::reset)
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
	}

	fun onTick(client: Minecraft) {
		if (Utils.getLocation() != Island.MINESHAFT) return
		val entities = client.level?.entitiesForRendering() ?: return

		if (corpses.size < getTotalCorpses()) {
			corpses = entities.filterIsInstance<ArmorStand>().filter(::isCorpse).map {
				Corpse(it, getColorFromHelmet(it.getItemBySlot(EquipmentSlot.HEAD)))
			}
		}
	}

	fun onRenderWorld(context: WorldRenderContext) {
		if (!config.box && !config.tracer) return
		if (corpses.isEmpty()) return

		for (corpse in corpses) {
			if (config.box) context.drawBoundingBox(corpse.entity.boundingBox, corpse.color)
			if (config.tracer) context.drawTracer(corpse.entity.eyePosition, corpse.color)
		}
	}

	fun reset(level: ClientLevel) {
		corpses = emptyList()
	}

	fun isCorpse(entity: ArmorStand): Boolean {
		return entity.getItemBySlot(EquipmentSlot.HEAD).item.equalsOneOf(Items.LEATHER_HELMET, Items.SEA_LANTERN)
	}

	fun getTotalCorpses(): Int {
		return TabListUtil.getTabList().count { it.second.string.matches(corpseRegex) }
	}

	fun getColorFromHelmet(stack: ItemStack): ChromaColour {
		if (!config.corpseColor) return config.color

		return when (stack.item) {
			Items.SEA_LANTERN -> ChromaColour.fromStaticRGB(0, 0, 255, 255)
			Items.LEATHER_HELMET -> stack.get(DataComponents.DYED_COLOR)?.rgb?.toChromaColour() ?: config.color
			else -> config.color
		}
	}
}