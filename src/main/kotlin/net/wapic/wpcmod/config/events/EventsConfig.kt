package net.wapic.wpcmod.config.events

import io.github.notenoughupdates.moulconfig.annotations.Category

class EventsConfig {

	@Category(name = "Diana", desc = "Mythological Ritual Config")
	var diana: DianaConfig = DianaConfig()
}