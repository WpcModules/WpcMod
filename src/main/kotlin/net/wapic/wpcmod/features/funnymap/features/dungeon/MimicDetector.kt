package net.wapic.wpcmod.features.funnymap.features.dungeon

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.util.math.BlockPos
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.features.funnymap.FunnyMap.mc
import net.wapic.wpcmod.features.funnymap.features.dungeon.ScanUtils.getRoomFromPos
import net.wapic.wpcmod.util.ItemUtils.headTexture
import net.wapic.wpcmod.util.Utils

object MimicDetector {

	val FunnyConfig get() = WpcMod.config.funnyMap

	var mimicOpenTime = 0L
	var mimicPos: BlockPos? = null

	fun onBlockChange(pos: BlockPos, old: BlockState, new: BlockState) {
		if (old.block == Blocks.TRAPPED_CHEST && new.block == Blocks.AIR) {
			mimicOpenTime = System.currentTimeMillis()
			mimicPos = pos
		}
	}

	fun checkMimicDead() {
		if (RunInformation.mimicKilled) return
		if (mimicOpenTime == 0L) return
		if (System.currentTimeMillis() - mimicOpenTime < 750) return

		val playerDistanceFromMimic = mc.player?.squaredDistanceTo(mimicPos?.toCenterPos()) ?: return

		if (playerDistanceFromMimic < 400.0) {
			val isMimicDead = mc.world?.entities?.none {
				it is ZombieEntity && it.isBaby && it.getEquippedStack(EquipmentSlot.HEAD).headTexture == "bcb486a4-0cb5-35db-93f0-039fbdde03f0"
			}
			if (isMimicDead == true)
				setMimicKilled()
		}
	}

	fun setMimicKilled() {
		RunInformation.mimicKilled = true
	}

	fun isMimic(entity: Entity): Boolean {
		return entity is ZombieEntity && entity.isBaby
	}

	fun findMimic(): String? {
		Utils.getLoadedBlockEntities().filter { it is ChestBlockEntity && it.type == BlockEntityType.TRAPPED_CHEST }
			.groupingBy { getRoomFromPos(it.pos)?.data?.name }.eachCount()
			.forEach { (room, trappedChests) ->
				Dungeon.Info.uniqueRooms.find { it.name == room && it.mainRoom.data.trappedChests < trappedChests }
					?.let {
						it.hasMimic = true
						MapRenderList.renderUpdated = true
						return it.name
					}
			}
		return null
	}
}
