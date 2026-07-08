package net.wapic.wpcmod.util

enum class Island(val displayName: String = "", val internalName: String = "") {
	UNKNOWN,

	// Miscellaneous
	HUB("Hub", "hub"),
	DUNGEON_HUB("Dungeon Hub", "dungeon_hub"),
	PRIVATE_ISLAND("Private Island", "dynamic"),
	RIFT("The Rift", "rift"),
	JERRY_WORKSHOP("Jerry's Workshop", "winter_island"),

	// Foraging
	PARK("The Park", "foraging_1"),
	GALATEA("Galatea", "foraging_2"),

	// Mining
	GOLD_MINE("Gold Mine", "mining_1"),
	DEEP_CAVERNS("Deep Caverns", "mining_2"),
	DWARVEN_MINES("Dwarven Mines", "mining_3"),
	CRYSTAL_HOLLOWS("Crystal Hollows", "crystal_hollows"),
	MINESHAFT("Mineshaft", "mineshaft"),

	// Fishing
	BACKWATER_BAYOU("Backwater Bayou", "fishing_1"),
	LOTUS_ATOLL("Lotus Atoll", "fishing_2"),

	// Combat
	SPIDER_DEN("Spider's Den", "combat_1"),
	END("The End", "combat_3"),
	CRIMSON_ISLE("Crimson Isle", "crimson_isle"),

	// Farming
	BARN("The Farming Islands", "farming_1"),
	GARDEN("Garden", "garden"),

	// Instanced
	DUNGEON("Dungeon", "dungeon"),
	KUUDRA("Kuudra", "kuudra");

	companion object {

		fun fromDisplayName(displayName: String?): Island {
			return entries.find { it.displayName == displayName } ?: UNKNOWN
		}

		fun fromInternalName(internalName: String?): Island {
			return entries.find { it.internalName == internalName } ?: UNKNOWN
		}
	}
}