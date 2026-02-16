package net.wapic.wpcmod.features.dungeons

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.level.block.Blocks
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.PlayerPickEvents
import net.wapic.wpcmod.mixin.accessors.MinecraftAccessor
import net.wapic.wpcmod.util.DungeonUtils
import net.wapic.wpcmod.util.ItemUtils.skyBlockID
import net.wapic.wpcmod.util.MC
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object EasySuperboom {

	val config get() = WpcMod.config.dungeon

	const val SUPERBOOM_TNT = "SUPERBOOM_TNT"
	val boomBlocks = hashSetOf(Blocks.GOLD_BLOCK, Blocks.SMOOTH_STONE_SLAB, Blocks.CRACKED_STONE_BRICKS)

	fun init() {
		PlayerPickEvents.BLOCK.register(::onPickBlock)
	}

	fun onPickBlock(pos: BlockPos, ci: CallbackInfo) {
		if (!config.easySuperboom || !DungeonUtils.inDungeons) return

		val state = MC.world?.getBlockState(pos) ?: return
		if (!boomBlocks.contains(state.block)) return

		val slot = MC.player?.inventory?.indexOfFirst { it.skyBlockID == SUPERBOOM_TNT } ?: return

		if (Inventory.isHotbarSlot(slot)) {
			MC.player?.inventory?.selectedSlot = slot
			MC.runOnThread { (MC.instance as? MinecraftAccessor)?.doItemUse_WpcMod() }
			ci.cancel()
		}
	}
}