package net.wapic.wpcmod.util

enum class Island(val displayName: String, val internalName: String) {
	HUB("Hub", "hub"),
	PARK("The Park", "foraging_1"),
	GALATEA("Galatea", "foraging_2"),
	GOLD_MINE("Gold Mine", "mining_1"),
	DEEP_CAVERNS("Deep Caverns", "mining_2"),
	DWARVEN_MINES("Dwarven Mines", "mining_3"),
	CRYSTAL_HOLLOWS("Crystal Hollows", "crystal_hollows"),
	BACKWATER_BAYOU("Backwater Bayou", "fishing_1"),
	SPIDER_DEN("Spider's Den", "combat_1"),
	END("The End", "combat_3"),
	CRIMSON_ISLE("Crimson Isle", "crimson_isle"),
	RIFT("The Rift", "rift"),
	PRIVATE_ISLAND("Private Island", "dynamic"),
	BARN("The Farming Islands", "farming_1"),
	GARDEN("Garden", "garden"),
	DUNGEON_HUB("Dungeon Hub", "dungeon_hub"),
	DUNGEON("Dungeon", "dungeon"),
	KUUDRA("Kuudra", "kuudra");

	companion object {
		fun fromDisplayName(displayName: String): Island? {
			return entries.find { it.displayName == displayName }
		}

		fun fromInternalName(internalName: String): Island? {
			return entries.find { it.internalName == internalName }
		}
	}
}