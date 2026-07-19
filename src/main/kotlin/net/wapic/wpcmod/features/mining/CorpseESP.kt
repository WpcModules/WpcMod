package net.wapic.wpcmod.features.mining

import io.github.notenoughupdates.moulconfig.ChromaColour
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.item.Items
import net.minecraft.world.phys.AABB
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.WorldChangeEvent
import net.wapic.wpcmod.features.entity.EspFeature
import net.wapic.wpcmod.util.copyWithColor
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.skyblockId
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils
import net.wapic.wpcmod.util.render.state.EspRenderState
import java.util.*

object CorpseESP : EspFeature() {

	private val config get() = WpcMod.config.mining.esp.corpse
	private val corpses = hashMapOf<UUID, Corpse>()

	private val corpseRegex = Regex("^ (Lapis|Umber|Tungsten): (NOT )?LOOTED$")
	private val corpseLootedRegex = Regex("^\\s+(LAPIS|UMBER|TUNGSTEN) CORPSE LOOT!\\s+$")

	private val helmetItemColors = mapOf(
		Items.SEA_LANTERN to ChromaColour.fromStaticRGB(0, 0, 170, 200),
		Items.LEATHER_HELMET to ChromaColour.fromStaticRGB(255, 170, 0, 200),
		Items.PLAYER_HEAD to ChromaColour.fromStaticRGB(170, 170, 170, 200)
	)

	private val corpseHelmetIds = arrayOf("LAPIS_ARMOR_HELMET", "ARMOR_OF_YOG_HELMET", "MINERAL_HELMET")

	data class Corpse(val boundingBox: AABB, var isLooted: Boolean = false)

	fun init() {
		WorldChangeEvent.BEFORE.register(::reset)
		ClientReceiveMessageEvents.GAME.register(::onMessageReceived)
	}

	private fun onMessageReceived(text: Component, actionBar: Boolean) {
		if (actionBar || corpses.isEmpty() || Utils.getLocation() != Island.MINESHAFT) return

		if (corpseLootedRegex.matches(text.string)) {
			val playerPos = MC.player?.position() ?: return
			corpses.values.minByOrNull { it.boundingBox.center.distanceTo(playerPos) }?.isLooted = true
		}
	}

	private fun reset(level: ClientLevel) = corpses.clear()

	private fun getCorpseColor(entity: ArmorStand) =
		helmetItemColors[entity.getItemBySlot(EquipmentSlot.HEAD).item] ?: config.color

	private fun isCorpse(entity: ArmorStand) = entity.getItemBySlot(EquipmentSlot.HEAD).skyblockId in corpseHelmetIds

	override fun compute(entity: Entity): EspRenderState? {
		val armorStand = entity as? ArmorStand ?: return null
		if (!isCorpse(armorStand)) return null

		val corpse = corpses.getOrPut(armorStand.uuid) { Corpse(armorStand.boundingBox) }
		val config = config.takeUnless { corpse.isLooted } ?: return null

		return EspRenderState.fromEntity(entity, if(config.corpseColor) config.copyWithColor(getCorpseColor(armorStand)) else config)
	}

	override fun isEnabled(): Boolean =
		Utils.getLocation() == Island.MINESHAFT && (config.glow || config.tracer || config.box)
}