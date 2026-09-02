package net.wapic.wpcmod.util

import net.minecraft.ChatFormatting
import net.minecraft.core.Holder
import net.minecraft.resources.Identifier
import net.minecraft.world.level.biome.Biome

object SafariAPI {
	val inSafari get() = Utils.getLocation() == Island.SAFARI

	enum class SafariBiome(val identifier: Identifier, val color: ChatFormatting) {
		ICY(Identifier.fromNamespaceAndPath("hypixel", "icy"), ChatFormatting.AQUA),
		ICY_CAVES(Identifier.fromNamespaceAndPath("hypixel", "icy_caves"), ChatFormatting.AQUA),
		CAVERN(Identifier.fromNamespaceAndPath("hypixel", "cavern"), ChatFormatting.GOLD),
		FOREST(Identifier.fromNamespaceAndPath("hypixel", "forest"), ChatFormatting.DARK_GREEN),
		HAUNTED(Identifier.fromNamespaceAndPath("hypixel", "haunted"), ChatFormatting.DARK_PURPLE);

		companion object {
			fun fromBiome(biome: Holder<Biome>) = entries.find { biome.isOf(it) }

			fun Holder<Biome>.isOf(safariBiome: SafariBiome): Boolean = this.`is`(safariBiome.identifier)
			fun Holder<Biome>.isIcyBiome(): Boolean = this.registeredName.startsWith(ICY.identifier.toString())
			fun Holder<Biome>.isSimilarTo(other: Holder<Biome>): Boolean {
				val areBothIcyBiomes = this.isIcyBiome() && other.isIcyBiome()
				return this == other || areBothIcyBiomes
			}

			fun SafariBiome.isSimilarTo(biome: Holder<Biome>): Boolean {
				val areBothIcyBiomes = (this == ICY || this == ICY_CAVES) && biome.isIcyBiome()
				return biome.isOf(this) || areBothIcyBiomes
			}
		}
	}

	enum class Critter(val entityName: String, val biome: SafariBiome) {
		STRONGARM("Strongarm", SafariBiome.ICY),
		TEPID("Tepid", SafariBiome.ICY_CAVES),
		POLARIS("Polaris", SafariBiome.ICY_CAVES),
		SHUDDERSQUID("Shuddersquid", SafariBiome.ICY_CAVES),
		BILLYGOAT("Billygoat", SafariBiome.ICY),
		MANTIS_SHRIMP("Mantis Shrimp", SafariBiome.ICY_CAVES),
		NOZZLENOSE("Nozzlenose", SafariBiome.ICY_CAVES),
		TROODON("Troodon", SafariBiome.ICY_CAVES),
		WUMPA("Wumpa", SafariBiome.ICY_CAVES),

		AREITA("Areita", SafariBiome.HAUNTED),
		BLOODBAT("Bloodbat", SafariBiome.HAUNTED),
		DUPLICO("Duplico", SafariBiome.HAUNTED),
		GAZER("Gazer", SafariBiome.HAUNTED),
		LITTERBUG("Litterbug", SafariBiome.HAUNTED),
		SOLSNATCHER("Solsnatcher", SafariBiome.HAUNTED),
		GIMMIEGOLD("Gimmiegold", SafariBiome.HAUNTED),
		HIDEONWALL("Hideonwall", SafariBiome.HAUNTED),
		HIDEYHO("Hideyho", SafariBiome.HAUNTED),
		DOOMSPIRAL("Doomspiral", SafariBiome.HAUNTED),

		FOXTROT("Foxtrot", SafariBiome.FOREST),
		BLUEBIRD("Bluebird", SafariBiome.FOREST),
		HONEYBUG("Honeybug", SafariBiome.FOREST),
		TREEFROG("Treefrog", SafariBiome.FOREST),
		WOODCHUCKER("Woodchucker", SafariBiome.FOREST),
		FLUFFLING("Fluffling", SafariBiome.FOREST),
		HIDEONFLOOR("Hideonfloor", SafariBiome.FOREST),
		PARAKEET("Parakeet", SafariBiome.FOREST),
		MACAW("Macaw", SafariBiome.FOREST),

		CAVERNFISH("Cavernfish", SafariBiome.CAVERN),
		FLITTER("Flitter", SafariBiome.CAVERN),
		SHYWORM("Shyworm", SafariBiome.CAVERN),
		DRIFTLING("Driftling", SafariBiome.CAVERN),
		CHUCKWALLA("Chuckwalla", SafariBiome.CAVERN),
		ROCKMITE("Rockmite", SafariBiome.CAVERN),
		SCRAPPY("Scrappy", SafariBiome.CAVERN),
		SNOOZLE("Snoozle", SafariBiome.CAVERN),
		GEMZIE("Gemzie", SafariBiome.CAVERN);

		companion object {
			fun fromName(name: String): Critter? = entries.find { it.entityName == name }
		}
	}
}