package net.wapic.wpcmod.features.funnymap.dungeon

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.util.math.BlockPos
import net.wapic.wpcmod.features.dungeons.ScoreCalculation
import net.wapic.wpcmod.features.funnymap.dungeon.ScanUtils.getRoomFromPos
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils

object MimicDetector {

	var mimicOpenTime = 0L
	var mimicPos: BlockPos? = null

	fun onBlockChange(pos: BlockPos, old: BlockState, new: BlockState) {
		if (old.block == Blocks.TRAPPED_CHEST && new.block == Blocks.AIR) {
			mimicOpenTime = System.currentTimeMillis()
			mimicPos = pos
		}
	}

	fun checkMimicDead() {
		if (mimicOpenTime == 0L) return
		if (System.currentTimeMillis() - mimicOpenTime < 750) return

		val playerDistanceFromMimic = MC.player?.squaredDistanceTo(mimicPos?.toCenterPos()) ?: return

		if (playerDistanceFromMimic < 400.0) {
			val isMimicDead = MC.world?.entities?.none { it is ZombieEntity && it.isBaby }
			if (isMimicDead == true)
				ScoreCalculation.mimicFound = true
		}
	}

	fun findMimic(): String? {
		Utils.getLoadedBlockEntities().filter { it is ChestBlockEntity && it.type == BlockEntityType.TRAPPED_CHEST }
			.groupingBy { getRoomFromPos(it.pos)?.data?.name }.eachCount()
			.forEach { (room, trappedChests) ->
				Dungeon.Info.uniqueRooms.find { it.name == room && it.mainRoom.data.trappedChests < trappedChests }
					?.let {
						it.hasMimic = true
						return it.name
					}
			}
		return null
	}
}
