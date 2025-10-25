package net.wapic.wpcmod.features.mining

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.ItemUtils.skyBlockID
import net.wapic.wpcmod.util.Utils

object PigeonSwapper {

	private val config get() = WpcMod.config.mining
	private val allowedAreas: List<Island> = listOf(Island.DWARVEN_MINES, Island.CRYSTAL_HOLLOWS)

	fun init() {
		UseItemCallback.EVENT.register { player, world, hand -> onUse(player, world, hand) }
		UseBlockCallback.EVENT.register { player, world, hand, _ -> onUse(player, world, hand)}
	}

	private fun onUse(player: PlayerEntity, world: World, hand: Hand): ActionResult {
		if (!config.pigeonSwapper || Utils.getLocation() !in allowedAreas) return ActionResult.PASS
		if (player.mainHandStack?.skyBlockID != "ROYAL_PIGEON") return ActionResult.PASS

		val inventory = player.inventory ?: return ActionResult.PASS
		val drillItem = inventory.find { it.name.string.contains("Drill") } ?: return ActionResult.PASS

		inventory.selectedSlot = inventory.getSlotWithStack(drillItem)
		return ActionResult.SUCCESS
	}
}