package net.wapic.wpcmod.features.mining

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.world.entity.player.Player
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.Level
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.ItemUtils.skyBlockID
import net.wapic.wpcmod.util.Utils

object PigeonSwapper {

	private val config get() = WpcMod.config.mining
	private val allowedAreas: List<Island> = listOf(Island.DWARVEN_MINES, Island.CRYSTAL_HOLLOWS, Island.MINESHAFT)

	fun init() {
		UseItemCallback.EVENT.register { player, world, hand -> onUse(player, world, hand) }
		UseBlockCallback.EVENT.register { player, world, hand, _ -> onUse(player, world, hand)}
	}

	private fun onUse(player: Player, world: Level, hand: InteractionHand): InteractionResult {
		if (!config.pigeonSwapper || Utils.getLocation() !in allowedAreas) return InteractionResult.PASS
		if (player.mainHandItem?.skyBlockID != "ROYAL_PIGEON") return InteractionResult.PASS

		val inventory = player.inventory ?: return InteractionResult.PASS
		val drillItem = inventory.find { it.hoverName.string.contains("Drill") } ?: return InteractionResult.PASS

		inventory.selectedSlot = inventory.findSlotMatchingItem(drillItem)
		return InteractionResult.SUCCESS
	}
}