package net.wapic.wpcmod.features.mining

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.item.Items
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.TabListUtil
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.Utils.equalsOneOf
import net.wapic.wpcmod.util.render.WorldRenderContext

object CorpseESP {

	val config get() = WpcMod.config.mining.esp.corpse
	var corpses: List<Corpse> = emptyList()
	val corpseRegex = Regex("^ (Lapis|Umber|Tungsten): (NOT )?LOOTED$")
	val corpseLootedRegex = Regex("^\\s+(LAPIS|UMBER|TUNGSTEN) CORPSE LOOT!\\s+$")
	val corpseToColor = mapOf(
		Items.SEA_LANTERN to ChromaColour.fromStaticRGB(0, 0, 170, 255),
		Items.LEATHER_HELMET to ChromaColour.fromStaticRGB(255, 170, 0, 255),
		Items.PLAYER_HEAD to ChromaColour.fromStaticRGB(170, 170, 170, 255)
	)

	data class Corpse(val entity: ArmorStand, val color: ChromaColour, var isLooted: Boolean)

	fun init() {
		WorldRenderEvent.EVENT.register(::onRenderWorld)
		WorldChangeEvent.BEFORE.register(::reset)
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	fun onMessageReceived(text: Component, actionBar: Boolean) {
		if (actionBar || Utils.getLocation() != Island.MINESHAFT) return
		if (corpseLootedRegex.matches(text.string) && corpses.isNotEmpty()) {
			val player = MC.player ?: return
			corpses.minByOrNull { it.entity.distanceTo(player) }?.isLooted = true
		}
	}

	fun onTick(client: Minecraft) {
		if (Utils.getLocation() != Island.MINESHAFT) return
		if (!config.box && !config.tracer) return
		val level = client.level ?: return

		if (corpses.size < getTotalCorpses()) {
			corpses = level.entitiesForRendering().filterIsInstance<ArmorStand>().filter(::isCorpse).map {
				Corpse(it, corpseToColor[it.getItemBySlot(EquipmentSlot.HEAD).item] ?: config.color, false)
			}
		}
	}

	fun onRenderWorld(context: WorldRenderContext) {
		if (Utils.getLocation() != Island.MINESHAFT || corpses.isEmpty()) return
		if (!config.box && !config.tracer) return

		for (corpse in corpses) {
			if (corpse.isLooted) continue
			if (config.box) context.drawBoundingBox(corpse.entity.boundingBox, corpse.color)
			if (config.tracer) context.drawTracer(corpse.entity.eyePosition, corpse.color)
		}
	}

	fun reset(level: ClientLevel) {
		corpses = emptyList()
	}

	fun isCorpse(entity: ArmorStand): Boolean {
		return entity.getItemBySlot(EquipmentSlot.HEAD).item.equalsOneOf(
			Items.PLAYER_HEAD,
			Items.LEATHER_HELMET,
			Items.SEA_LANTERN
		)
	}

	fun getTotalCorpses(): Int {
		return TabListUtil.getTabList().count { corpseRegex.matches(it.second.string) }
	}
}