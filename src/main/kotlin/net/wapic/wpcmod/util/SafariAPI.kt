package net.wapic.wpcmod.util

import net.minecraft.core.Holder
import net.minecraft.resources.Identifier
import net.minecraft.world.level.biome.Biome

object SafariAPI {
	val inSafari get() = Utils.getLocation() == Island.SAFARI

	enum class SafariBiome(val identifier: Identifier) {
		ICY(Identifier.fromNamespaceAndPath("hypixel", "icy")),
		ICY_CAVES(Identifier.fromNamespaceAndPath("hypixel", "icy_caves")),
		CAVERN(Identifier.fromNamespaceAndPath("hypixel", "cavern")),
		FOREST(Identifier.fromNamespaceAndPath("hypixel", "forest")),
		HAUNTED(Identifier.fromNamespaceAndPath("hypixel", "haunted"));

		companion object {
			fun fromBiome(biome: Holder<Biome>) = entries.find { biome.isOf(it) }
			fun Holder<Biome>.isOf(safariBiome: SafariBiome): Boolean = this.`is`(safariBiome.identifier)
		}
	}
}