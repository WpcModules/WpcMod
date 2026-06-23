package net.wapic.wpcmod.config.dungeon

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.*
import net.wapic.wpcmod.features.dungeons.floor7.termsim.StartGUI

class Floor7Config {

	@ConfigOption(name = "Melody Message", desc = "Helpful messages for the melody terminal in floor 7.")
	@ConfigEditorBoolean
	var melodyMessage: Boolean = false

	@ConfigOption(name = "Terminal Simulator Ping", desc = "Set emulated ping for terminal simulators")
	@ConfigEditorSlider(minValue = 1f, maxValue = 300f, minStep = 1f)
	var termSimPing: Float = 50f

	@Transient
	@ConfigOption(name = "Terminal Simulator", desc = "Open terminal simulator, can also be accessed with /wpc term.")
	@ConfigEditorButton(buttonText = "Open")
	var openTerminalSimulator: Runnable = Runnable { StartGUI.open(termSimPing.toLong()) }

	@Accordion
	@ConfigOption(name = "Arrow Align Solver", desc = "Solver for Arrow Device in P3")
	var arrowAlign: ArrowAlignConfig = ArrowAlignConfig()

	class ArrowAlignConfig {

		@ConfigOption(name = "Enabled", desc = "Global toggle for Arrow Align solver")
		@ConfigEditorBoolean
		var enabled: Boolean = false

		@ConfigOption(name = "Block Wrong Clicks", desc = "")
		@ConfigEditorBoolean
		var blockWrongClick = false

		@ConfigOption(name = "Invert Sneak", desc = "Only block wrong clicks whilst sneaking, instead of whilst standing")
		@ConfigEditorBoolean
		var invertSneak = false
	}

	@Accordion
	@ConfigOption(name = "Tick Timers", desc = "Displays timers for Necron, Goldor, and Storm.")
	var tickTimers: TickTimerConfig = TickTimerConfig()

	class TickTimerConfig {

		@ConfigOption(name = "Enabled", desc = "Global toggle for tick timers")
		@ConfigEditorBoolean
		var enabled: Boolean = false

		@ConfigOption(name = "Display in Ticks", desc = "Display the timers in ticks instead of seconds.")
		@ConfigEditorBoolean
		var displayInTicks: Boolean = false

		@ConfigOption(name = "Display Symbol", desc = "Displays s or t after the timers.")
		@ConfigEditorBoolean
		var symbolDisplay: Boolean = true

		@ConfigOption(name = "Show Prefix", desc = "Shows the prefix of the timers.")
		@ConfigEditorBoolean
		var showPrefix: Boolean = true

		@ConfigOption(name = "Start timer", desc = "Displays a timer counting down until devices/terms are able to be activated/completed.")
		@ConfigEditorBoolean
		var startTimer: Boolean = false
	}

	@Accordion
	@ConfigOption(name = "Terminal Solver", desc = "Renders solution for terminals in floor 7.")
	var terminalSolvers: TerminalSolverConfig = TerminalSolverConfig()

	class TerminalSolverConfig {

		@ConfigOption(name = "Enable", desc = "Global Toggle for Terminal Solvers")
		@ConfigEditorBoolean
		var enabled = false

		@ConfigOption(name = "Debug Mode", desc = "Render debug info for terminals.")
		@ConfigEditorBoolean
		var debug = false

		@ConfigOption(name = "Hide Clicked", desc = "Visually hides your first click before a gui updates instantly to improve perceived response time. Does not affect actual click time.")
		@ConfigEditorBoolean
		var hideClicked: Boolean = false

		@ConfigOption(name = "Show Numbers", desc = "Shows numbers in the order terminal.")
		@ConfigEditorBoolean
		var showNumbers: Boolean = true

		@ConfigOption(name = "Show Entire Order Terminal", desc = "Show all numbers in the order terminal")
		@ConfigEditorBoolean
		var showAllOrder: Boolean = true

		@ConfigOption(name = "Reload Threshold", desc = "Time in milliseconds before the terminal refreshes.")
		@ConfigEditorSlider(minValue = 300f, maxValue = 1000f, minStep = 10f)
		var terminalReloadThreshold: Float = 600f

		@ConfigOption(name = "Custom Term Scale", desc = "The scale of the custom terminal GUI")
		@ConfigEditorSlider(minValue = 0.5f, maxValue = 6f, minStep = 0.1f)
		var customTermSize: Float = 2f

		@ConfigOption(name = "Gap",	desc = "The gap between the slots in the custom terminal gui.")
		@ConfigEditorSlider(minValue = 0f, maxValue = 8f, minStep = 1f)
		var gap: Float = 2f

		@ConfigOption(name = "Background Roundness", desc = "The corner roundness of the terminal")
		@ConfigEditorSlider(minValue = 0f, maxValue = 32f, minStep = 1f)
		var backgroundRoundness: Float = 16f

		@ConfigOption(name = "Slot Roundness", desc = "The slot corner roundness of the terminal")
		@ConfigEditorSlider(minValue = 0f, maxValue = 8f, minStep = 1f)
		var slotRoundness: Float = 4f

		@ConfigOption(name = "Background", desc = "Background color of the terminal solver.")
		@ConfigEditorColour
		var backgroundColor = ChromaColour.fromRGB(0, 0, 0, 0, 125)

		@ConfigOption(name = "Panes",	desc = "Color of the panes terminal solver.")
		@ConfigEditorColour
		var panesColor = ChromaColour.fromRGB(0, 255, 255, 0, 255)

		@ConfigOption(name = "Rubix 1", desc = "Color of the rubix terminal solver for 1 click.")
		@ConfigEditorColour
		var rubixColor1 = ChromaColour.fromRGB(0, 255, 255, 0, 255)

		@ConfigOption(name = "Rubix 2", desc = "Color of the rubix terminal solver for 2 click.")
		@ConfigEditorColour
		var rubixColor2 = ChromaColour.fromRGB(0, 255, 255, 0, 125)

		@ConfigOption(name = "Rubix -1", desc = "Color of the rubix terminal solver for -1 click.")
		@ConfigEditorColour
		var oppositeRubixColor1 = ChromaColour.fromRGB(255, 0, 0, 0, 255)
		
		@ConfigOption(name = "Rubix -2", desc = "Color of the rubix terminal solver for -2 click.")
		@ConfigEditorColour
		var oppositeRubixColor2 = ChromaColour.fromRGB(255, 0, 0, 0, 125)

		@ConfigOption(name = "Order 1", desc = "Color of the order terminal solver for 1st item.")
		@ConfigEditorColour
		var orderColor = ChromaColour.fromRGB(0, 255, 255, 0, 255)

		@ConfigOption(name = "Order 2", desc = "Color of the order terminal solver for 2nd item.")
		@ConfigEditorColour
		var orderColor2 = ChromaColour.fromRGB(0, 255, 255, 0, 170)

		@ConfigOption(name = "Order 3", desc = "Color of the order terminal solver for 3rd item.")
		@ConfigEditorColour
		var orderColor3 = ChromaColour.fromRGB(0, 255, 255, 0, 85)

		@ConfigOption(name = "Starts With", desc = "Color of the starts with terminal solver.")
		@ConfigEditorColour
		var startsWithColor = ChromaColour.fromRGB(0, 255, 255, 0, 255)

		@ConfigOption(name = "Select", desc = "Color of the select terminal solver.")
		@ConfigEditorColour
		var selectColor = ChromaColour.fromRGB(0, 255, 255, 0, 255)

		@ConfigOption(name = "Melody Column", desc = "Color of the colum indicator for melody.")
		@ConfigEditorColour
		var melodyColumColor = ChromaColour.fromRGB(255, 0, 255, 0, 255)

		@ConfigOption(name = "Melody Row", desc = "Color of the row indicator for melody.")
		@ConfigEditorColour
		var melodyRowColor = ChromaColour.fromRGB(255, 0, 0, 0, 255)

		@ConfigOption(name = "Melody Pointer", desc = "Color of the location for pressing for melody.")
		@ConfigEditorColour
		var melodyPointerColor = ChromaColour.fromRGB(0, 255, 0, 0, 255)
	}

	@Accordion
	@ConfigOption(name = "Inactive Waypoints", desc = "Shows inactive terminals, devices and levers.")
	var inactiveWaypoints: InactiveWaypointsConfig = InactiveWaypointsConfig()

	class InactiveWaypointsConfig {
		@ConfigOption(name = "Enabled", desc = "Global toggle for Inactive Waypoints")
		@ConfigEditorBoolean
		var enabled: Boolean = false

		@ConfigOption(name = "Show Terminals", desc = "Shows inactive terminals.")
		@ConfigEditorBoolean
		val showTerminals: Boolean = true

		@ConfigOption(name = "Show Devices", desc = "Shows inactive devices.")
		@ConfigEditorBoolean
		val showDevices: Boolean = true

		@ConfigOption(name = "Show Levers",desc = "Shows inactive levers.")
		@ConfigEditorBoolean
		val showLevers: Boolean = true

		@ConfigOption(name = "Render Text",desc = "Renders the name of the inactive waypoint.")
		@ConfigEditorBoolean
		val renderText: Boolean = true

		@ConfigOption(name = "Render Box",desc = "Renders a box around the inactive waypoint.")
		@ConfigEditorBoolean
		val renderBox: Boolean = true

		@ConfigOption(name = "Hide Default",desc = "Hide the Hypixel names of Inactive Terminals.")
		@ConfigEditorBoolean
		val hideDefault: Boolean = true

		@ConfigOption(name = "Color", desc = "The color of the box.")
		@ConfigEditorColour
		val color: ChromaColour = ChromaColour(1f, 1f, 1f, 0, 255)
	}

	@Accordion
	@ConfigOption(name = "Auto Debuff", desc = "")
	var debuff: AutoDebuffConfig = AutoDebuffConfig()

	class AutoDebuffConfig {
		@ConfigOption(name = "Enable Auto Debuff", desc = "Automatically releases Last Breath at the correct height")
		@ConfigEditorBoolean
		var enabled: Boolean = false

		@ConfigOption(name = "Debuff Height", desc = "How many ticks to wait before releasing")
		@ConfigEditorSlider(minValue = 5f, maxValue = 15f, minStep = 1f)
		var releaseTick: Float = 11f
	}
}