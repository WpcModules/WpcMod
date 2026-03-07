package net.wapic.wpcmod.features.dungeons

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Inventory
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.PlayerPickEvents
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.ItemUtils.skyblockId
import net.wapic.wpcmod.util.MC
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object EasySuperboom {

	private val config get() = WpcMod.config.dungeon

	private const val SUPERBOOM_TNT = "SUPERBOOM_TNT"

	fun init() {
		PlayerPickEvents.BLOCK.register(::onPickBlock)
	}

	fun onPickBlock(pos: BlockPos, ci: CallbackInfo) {
		if (!config.easySuperboom || !DungeonUtils.inDungeons) return
		val playerInventory = MC.player?.inventory ?: return

		val slot = playerInventory.indexOfFirst { it.skyblockId == SUPERBOOM_TNT }

		if (Inventory.isHotbarSlot(slot)) {
			playerInventory.selectedSlot = slot
			MC.useItem()
			ci.cancel()
		}
	}
}