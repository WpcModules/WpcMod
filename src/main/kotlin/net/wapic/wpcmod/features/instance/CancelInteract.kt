package net.wapic.wpcmod.features.instance

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Island
import net.wapic.wpcmod.util.ItemUtils.skyBlockID
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils

object CancelInteract {
	private val config get() = WpcMod.config
	private const val ENDER_PEARL = "ENDER_PEARL"

	fun init() {
		UseBlockCallback.EVENT.register(::onItemUse)
	}

	fun onItemUse(player: Player, world: Level, hand: InteractionHand, block: BlockHitResult): InteractionResult {
		if(!(config.kuudra.cancelInteract && Utils.getLocation() == Island.KUUDRA) ||
			!(config.dungeon.cancelInteract && Utils.getLocation() == Island.DUNGEON))
		{
			return InteractionResult.PASS
		}

		if(player.mainHandItem.skyBlockID == ENDER_PEARL) {
			return MC.interactionManager?.useItem(player, hand) ?: InteractionResult.PASS
		}

		return InteractionResult.PASS
	}
}