package net.wapic.wpcmod.config.galatea

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.GlowableESPConfig
import net.wapic.wpcmod.config.components.NonGlowableESPConfig

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Shulker", desc = "Shulker Settings")
	var shulker = ShulkerConfig()

	class ShulkerConfig : GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Panda", desc = "Panda Settings")
	var panda = PandaConfig()

	class PandaConfig : GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Frog", desc = "Frog Settings")
	var frog = FrogConfig()

	class FrogConfig : GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Axolotl", desc = "Axolotl Settings")
	var axolotl = AxolotlConfig()

	class AxolotlConfig : GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Pufferfish", desc = "Pufferfish Settings")
	var pufferfish = PufferfishConfig()

	class PufferfishConfig : GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Shellwise", desc = "Shellwise Settings")
	var shellwise = ShellwiseConfig()

	class ShellwiseConfig : GlowableESPConfig()

	@Accordion
	@ConfigOption(name = "Invisibug", desc = "Invisibug Settings")
	var invisibug = InvisibugConfig()

	class InvisibugConfig : NonGlowableESPConfig()
}