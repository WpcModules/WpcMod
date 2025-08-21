package net.wapic.wpcmod.features.mining

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.world.World
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.ItemUtils.skyBlockID
import net.wapic.wpcmod.util.Utils
import kotlin.collections.find

class PigeonSwapper {

	private val config get() = WpcMod.config.mining

	init {
		UseItemCallback.EVENT.register(::onUseItem)
		UseBlockCallback.EVENT.register(::onUseBlock)
	}

	fun onUseItem(player: PlayerEntity, world: World, hand: Hand): ActionResult {
		return useItem(player, world, hand)
	}

	fun onUseBlock(player: PlayerEntity, world: World, hand: Hand, hitResult: BlockHitResult): ActionResult {
		return useItem(player, world, hand)
	}

	fun useItem(player: PlayerEntity, world: World, hand: Hand): ActionResult {
		if (!config.pigeonSwapper || player.mainHandStack.isEmpty) return ActionResult.PASS
		if(!(Utils.getLocation() == Island.DWARVEN_MINES || Utils.getLocation() == Island.CRYSTAL_HOLLOWS)) return ActionResult.PASS

		val item = player.mainHandStack.skyBlockID ?: return ActionResult.PASS

		if (item.contains("ROYAL_PIGEON".toRegex())) {
			player.inventory?.find { stack ->
				stack.name.string.contains("Drill")
			}?.let { stack ->
				player.inventory.setSelectedSlot(player.inventory.getSlotWithStack(stack))
				return ActionResult.SUCCESS
			}
		}

		return ActionResult.PASS
	}
}