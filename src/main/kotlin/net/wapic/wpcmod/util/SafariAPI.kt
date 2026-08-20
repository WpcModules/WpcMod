package net.wapic.wpcmod.util

import net.minecraft.core.BlockPos
import net.minecraft.world.level.levelgen.structure.BoundingBox

object SafariAPI {
	val inSafari get() = Utils.getLocation() == Island.SAFARI

	enum class Zone(val box: BoundingBox) {
		ICY(BoundingBox(-180, 28, -120, -51, 128, -1)),
		CAVERN(BoundingBox(-180, 28, 1, -51, 128, 120)),
		FOREST(BoundingBox(-49, 28, 1, 54, 128, 120)),
		HAUNTED(BoundingBox(-49, 28, -120, 54, 128, -1)),
		NONE(BoundingBox(-50, 28, 0, -50, 128, 0));

		companion object {
			fun fromBlockPos(blockPos: BlockPos?): Zone {
				blockPos ?: return NONE
				return Zone.entries.find { it.box.isInside(blockPos.x, blockPos.y, blockPos.z) } ?: NONE
			}
		}
	}
}