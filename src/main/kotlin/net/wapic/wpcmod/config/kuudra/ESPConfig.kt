package net.wapic.wpcmod.config.kuudra

import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.wapic.wpcmod.config.components.GlowableESPConfig

class ESPConfig {

	@Accordion
	@ConfigOption(name = "Kuudra", desc = "")
	var kuudra = KuudraConfig()

	class KuudraConfig(): GlowableESPConfig() {
		@ConfigOption(name = "Kill Phase Only", desc = "Only enable ESP during kill phase")
		@ConfigEditorBoolean
		var killPhaseOnly: Boolean = false
	}
}