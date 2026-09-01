package net.wapic.wpcmod.config.dungeon

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.*

class FunnyConfig {

	@ConfigOption(name = "Chat Info", desc = "Show dungeon overview information after scanning.")
	@ConfigEditorBoolean
	var scanChatInfo = true

	@ConfigOption(name = "Legit Mode", desc = "Hides unopened rooms. Still uses scanning to identify all rooms.")
	@ConfigEditorBoolean
	var legitMode = false

	@ConfigOption(name = "Map Enabled", desc = "Render the map!")
	@ConfigEditorBoolean
	var mapEnabled = true

	@ConfigOption(name = "Rotate Map", desc = "Rotates map to follow the player.")
	@ConfigEditorBoolean
	var mapRotate = false

	@ConfigOption(name = "Center Map", desc = "Centers the map on the player if Rotate Map is enabled.")
	@ConfigEditorBoolean
	var mapCenter = false

	@ConfigOption(name = "Hide In Boss", desc = "Hides the map in boss.")
	@ConfigEditorBoolean
	var hideInBoss = true

	@ConfigOption(
		name = "Show Player Names",
		desc = "Show player name under player head",
	)
	@ConfigEditorDropdown
	var playerHeads: PlayerNameType = PlayerNameType.OFF

	enum class PlayerNameType(val label: String) {
		OFF("Off"),
		HOLDING_LEAP("Holding Leap"),
		ALWAYS("Always");

		override fun toString(): String = label
	}

	@ConfigOption(name = "Vanilla Head Marker", desc = "Uses the vanilla head marker for yourself.")
	@ConfigEditorBoolean
	var mapVanillaMarker = false

	@ConfigOption(name = "Map Text Scale", desc = "Scale of room names and secret counts relative to map size.")
	@ConfigEditorSlider(minStep = 0.1f, maxValue = 2f, minValue = 0.3f)
	var textScale = .8f

	@ConfigOption(name = "Player Heads Scale", desc = "Scale of player heads relative to map size.")
	@ConfigEditorSlider(minStep = 0.1f, maxValue = 2f, minValue = 0.5f)
	var playerHeadScale = 1f

	@ConfigOption(name = "Player Name Scale", desc = "Scale of player names relative to head size.")
	@ConfigEditorSlider(minStep = 0.1f, maxValue = 2f, minValue = 0.5f)
	var playerNameScale = .8f

	@ConfigOption(name = "Dark Undiscovered Rooms", desc = "Darkens unentered rooms.")
	@ConfigEditorBoolean
	var mapDarkenUndiscovered = true

	@ConfigOption(name = "Darken Multiplier", desc = "How much to darken undiscovered rooms.")
	@ConfigEditorSlider(minStep = 0.1f, maxValue = 1f, minValue = 0f)
	var mapDarkenPercent = 0.5f

	@ConfigOption(name = "Gray Undiscovered Rooms", desc = "Grayscale unentered rooms.")
	@ConfigEditorBoolean
	var mapGrayUndiscovered = false

	@ConfigOption(name = "Room Names", desc = "Shows names of rooms on map.")
	@ConfigEditorBoolean
	var mapRoomNames = false

	@ConfigOption(name = "Center Room Names", desc = "Center room names.")
	@ConfigEditorBoolean
	var mapCenterRoomName = true

	@ConfigOption(name = "Room Checkmarks", desc = "Adds room checkmarks based on room state.")
	@ConfigEditorBoolean
	var mapCheckmark = true

	@ConfigOption(name = "Center Room Checkmarks", desc = "Center room checkmarks.")
	@ConfigEditorBoolean
	var mapCenterCheckmark = true

	@ConfigOption(name = "Draw Player Head Border", desc = "Draw A Border Around The Player Head")
	@ConfigEditorBoolean
	var drawHeadBorder = true

	@Accordion
	@ConfigOption(name = "Colors", desc = "Configure each color on the map")
	var colors: ColorConfig = ColorConfig()

	class ColorConfig {

		@ConfigOption(name = "Map Background Color", desc = "background color")
		@ConfigEditorColour
		var mapBackground = ChromaColour.fromRGB(90, 90, 90, 0, 100)

		@ConfigOption(name = "Map Border Color", desc = "border color")
		@ConfigEditorColour
		var mapBorder = ChromaColour.fromRGB(255, 255, 255, 0, 100)

		@ConfigOption(name = "Blood Door", desc = "blood door color")
		@ConfigEditorColour
		var colorBloodDoor = ChromaColour.fromRGB(252, 0, 0, 0, 255)

		@ConfigOption(name = "Entrance Door", desc = "entranceDoor color")
		@ConfigEditorColour
		var colorEntranceDoor = ChromaColour.fromRGB(0, 123, 0, 0, 255)

		@ConfigOption(name = "Normal Door", desc = "normal door color")
		@ConfigEditorColour
		var colorRoomDoor = ChromaColour.fromRGB(113, 66, 27, 0, 255)

		@ConfigOption(name = "Wither Door", desc = "wither door color")
		@ConfigEditorColour
		var colorWitherDoor = ChromaColour.fromRGB(13, 13, 13, 0, 255)

		@ConfigOption(name = "Opened Wither Door", desc = "opened wither door color")
		@ConfigEditorColour
		var colorOpenWitherDoor = ChromaColour.fromRGB(113, 66, 27, 0, 255)

		@ConfigOption(name = "Unopened Door", desc = "unopened door color")
		@ConfigEditorColour
		var colorUnopenedDoor = ChromaColour.fromRGB(64, 64, 64, 0, 255)

		@ConfigOption(name = "Blood Room", desc = "blood room color")
		@ConfigEditorColour
		var colorBlood = ChromaColour.fromRGB(252, 0, 0, 0, 255)

		@ConfigOption(name = "Entrance Room", desc = "Entrance Room color")
		@ConfigEditorColour
		var colorEntrance = ChromaColour.fromRGB(0, 123, 0, 0, 255)

		@ConfigOption(name = "Fairy Room", desc = "Fairy Room color")
		@ConfigEditorColour
		var colorFairy = ChromaColour.fromRGB(239, 125, 163, 0, 255)

		@ConfigOption(name = "Miniboss Room", desc = "miniboss room color")
		@ConfigEditorColour
		var colorMiniboss = ChromaColour.fromRGB(226, 226, 50, 0, 255)

		@ConfigOption(name = "Normal Room", desc = "Normal Room color")
		@ConfigEditorColour
		var colorRoom = ChromaColour.fromRGB(113, 66, 27, 0, 255)

		@ConfigOption(name = "Mimic Room", desc = "mimic room color")
		@ConfigEditorColour
		var colorRoomMimic = ChromaColour.fromRGB(255, 85, 85, 0, 255)

		@ConfigOption(name = "Puzzle Room", desc = "Puzzle Room color")
		@ConfigEditorColour
		var colorPuzzle = ChromaColour.fromRGB(176, 75, 213, 0, 255)

		@ConfigOption(name = "Rare Room", desc = "rare room color")
		@ConfigEditorColour
		var colorRare = ChromaColour.fromRGB(0, 255, 255, 0, 255)

		@ConfigOption(name = "Trap Room", desc = "Trap Room Color")
		@ConfigEditorColour
		var colorTrap = ChromaColour.fromRGB(213, 125, 50, 0, 255)

		@ConfigOption(name = "Unopened Room", desc = "Unopened Room color")
		@ConfigEditorColour
		var colorUnopened = ChromaColour.fromRGB(64, 64, 64, 0, 255)
	}
}