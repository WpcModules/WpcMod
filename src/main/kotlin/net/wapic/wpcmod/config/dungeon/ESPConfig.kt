package net.wapic.wpcmod.config.dungeon

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.GlowableESPConfig

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Starred Mobs", desc = "Any entity with a star in their name")
	var starMob = StarMobConfig()

	class StarMobConfig : GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Dungeon Keys", desc = "Wither and blood keys")
	var doorKeys = DoorKeysConfig()

	class DoorKeysConfig : GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Bats", desc = "Any Bat entity even non secret ones")
	var bat = BatConfig()

	class BatConfig : GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Mini Bosses", desc = "Mini Bosses are mobs like Lost Adventurer and Frozen Adventurer")
	var miniboss = MiniBossConfig()

	class MiniBossConfig : GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Wither Doors", desc = "Wither Doors")
	var witherDoor = WitherDoorConfig()

	class WitherDoorConfig {

		@ConfigOption(name = "Enable Wither Door ESP", desc = "Enables Wither Door ESP")
		@ConfigEditorBoolean
		var enabled: Boolean = false

		@ConfigOption(name = "Show All Doors", desc = "Show all doors or only the next door")
		@ConfigEditorBoolean
		var showAll: Boolean = false

		@ConfigOption(name = "Has Key Color", desc = "Color for when you have the key")
		@ConfigEditorColour
		var hasKeyColor = ChromaColour.fromRGB(0, 255, 0, 0, 128)

		@ConfigOption(name = "No Key Color", desc = "Color for when you don't have a key")
		@ConfigEditorColour
		var noKeyColor = ChromaColour.fromRGB(255, 0, 0, 0, 128)
	}
}