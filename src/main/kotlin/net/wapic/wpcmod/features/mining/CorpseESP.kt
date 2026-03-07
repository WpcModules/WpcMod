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
import net.minecraft.world.phys.AABB
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.events.WorldRenderEvent
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.ItemUtils.skyblockId
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.TabListUtil
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.WorldRenderContext
import net.wapic.wpcmod.util.render.darker
import java.util.*

object CorpseESP {

	private val config get() = WpcMod.config.mining.esp.corpse
	private val corpses = mutableMapOf<UUID, Corpse>()

	private val corpseRegex = Regex("^ (Lapis|Umber|Tungsten): (NOT )?LOOTED$")
	private val corpseLootedRegex = Regex("^\\s+(LAPIS|UMBER|TUNGSTEN) CORPSE LOOT!\\s+$")

	private val helmetItemColors = mapOf(
		Items.SEA_LANTERN to ChromaColour.fromStaticRGB(0, 0, 170, 200),
		Items.LEATHER_HELMET to ChromaColour.fromStaticRGB(255, 170, 0, 200),
		Items.PLAYER_HEAD to ChromaColour.fromStaticRGB(170, 170, 170, 200)
	)

	private val corpseHelmetIds = arrayOf("LAPIS_ARMOR_HELMET", "ARMOR_OF_YOG_HELMET", "MINERAL_HELMET")

	private val isActive: Boolean get() = corpses.isNotEmpty() && Utils.getLocation() == Island.MINESHAFT
	private val shouldRender: Boolean get() = config.box || config.tracer

	data class Corpse(val boundingBox: AABB, val color: ChromaColour, var isLooted: Boolean = false)

	fun init() {
		WorldRenderEvent.EVENT.register(::onRenderWorld)
		WorldChangeEvent.BEFORE.register(::reset)
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	private fun onMessageReceived(text: Component, actionBar: Boolean) {
		if (actionBar || !isActive) return
		if (corpseLootedRegex.matches(text.string)) {
			val playerPos = MC.player?.position() ?: return
			corpses.values.minByOrNull { it.boundingBox.center.distanceTo(playerPos) }?.isLooted = true
		}
	}

	private fun onTick(client: Minecraft) {
		if (Utils.getLocation() != Island.MINESHAFT) return
		if (corpses.size >= getTotalCorpses()) return

		MC.entitiesOf<ArmorStand>().filter { !corpses.containsKey(it.uuid) && isCorpse(it) }.forEach { entity ->
			corpses[entity.uuid] = Corpse(entity.boundingBox, getCorpseColor(entity))
		}
	}

	private fun onRenderWorld(context: WorldRenderContext) {
		if (!isActive || !shouldRender) return

		corpses.values.filterNot { it.isLooted }.forEach { corpse ->
			if (config.box) context.drawFilledBoxWithOutline(corpse.boundingBox, corpse.color.darker(), corpse.color)
			if (config.tracer) context.drawTracer(corpse.boundingBox.center, corpse.color)
		}
	}

	private fun reset(level: ClientLevel) {
		corpses.clear()
	}

	private fun getTotalCorpses() = TabListUtil.getTabList().count { corpseRegex.matches(it.second.string) }

	private fun getCorpseColor(entity: ArmorStand) =
		helmetItemColors[entity.getItemBySlot(EquipmentSlot.HEAD).item] ?: config.color

	private fun isCorpse(entity: ArmorStand) = entity.getItemBySlot(EquipmentSlot.HEAD).skyblockId in corpseHelmetIds
}