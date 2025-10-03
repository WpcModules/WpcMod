package net.wapic.wpcmod.config.dungeon

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.GlowableESPConfig

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Starred Mobs", desc = "Any entity with a star in their name")
	var starMob = StarMobConfig()

	class StarMobConfig(): GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Bats", desc = "Any Bat entity even non secret ones")
	var bat = BatConfig()

	class BatConfig(): GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Mini Bosses", desc = "Mini Bosses are mobs like Lost Adventurer and Frozen Adventurer")
	var miniboss = MiniBossConfig()

	class MiniBossConfig(): GlowableESPConfig()
}