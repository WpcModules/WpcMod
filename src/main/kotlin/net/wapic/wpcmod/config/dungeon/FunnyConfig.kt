package net.wapic.wpcmod.config.dungeon

import io.github.notenoughupdates.moulconfig.ChromaColour
import io.github.notenoughupdates.moulconfig.annotations.*

class FunnyConfig {

	@ConfigOption(
		name = "Auto Scan",
		desc = "Automatically scans when entering dungeon. Manual scan can be done with /fmap scan."
	)
	@ConfigEditorBoolean
	var autoScan = true

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
	var mapHideInBoss = false

	@ConfigOption(
		name = "Show Player Names",
		desc = "Show player name under player head",
	)
	@ConfigEditorDropdown
	var playerHeads: PlayerNameType = PlayerNameType.OFF

	enum class PlayerNameType {
		OFF,
		HOLDING_LEAP,
		ALWAYS;
	}

	@ConfigOption(name = "Vanilla Head Marker", desc = "Uses the vanilla head marker for yourself.")
	@ConfigEditorBoolean
	var mapVanillaMarker = false

	@ConfigOption(name = "Map Text Scale", desc = "Scale of room names and secret counts relative to map size.")
	@ConfigEditorSlider(minStep = 0.1f, maxValue = 2f, minValue = 0.1f)
	var textScale = .8f

	@ConfigOption(name = "Player Heads Scale", desc = "Scale of player heads relative to map size.")
	@ConfigEditorSlider(minStep = 0.1f, maxValue = 2f, minValue = 0.1f)
	var playerHeadScale = 1f

	@ConfigOption(name = "Player Name Scale", desc = "Scale of player names relative to head size.")
	@ConfigEditorSlider(minStep = 0.1f, maxValue = 2f, minValue = 0.1f)
	var playerNameScale = .8f

	@ConfigOption(name = "Map Background Color", desc = "background color")
	@ConfigEditorColour
	var mapBackground = ChromaColour(1f, 1f, 1f, 0, 100)

	@ConfigOption(name = "Map Border Color", desc = "border color")
	@ConfigEditorColour
	var mapBorder = ChromaColour(1f, 1f, 1f, 0, 100)

	@ConfigOption(name = "Dark Undiscovered Rooms", desc = "Darkens unentered rooms.")
	@ConfigEditorBoolean
	var mapDarkenUndiscovered = true

	@ConfigOption(name = "Darken Multiplier", desc = "How much to darken undiscovered rooms.")
	@ConfigEditorSlider(minStep = 0.1f, maxValue = 1f, minValue = 0.1f)
	var mapDarkenPercent = 0.4f

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
	var mapCheckmark = false

	@ConfigOption(name = "Center Room Checkmarks", desc = "Center room checkmarks.")
	@ConfigEditorBoolean
	var mapCenterCheckmark = true

	@ConfigOption(name = "Blood Door", desc = "blood door color")
	@ConfigEditorColour
	var colorBloodDoor = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Entrance Door", desc = "entranceDoor color")
	@ConfigEditorColour
	var colorEntranceDoor = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Normal Door", desc = "normal door color")
	@ConfigEditorColour
	var colorRoomDoor = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Wither Door", desc = "wither door color")
	@ConfigEditorColour
	var colorWitherDoor = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Opened Wither Door", desc = "opened wither door color")
	@ConfigEditorColour
	var colorOpenWitherDoor = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Unopened Door", desc = "unopened door color")
	@ConfigEditorColour
	var colorUnopenedDoor = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Blood Room", desc = "blood room color")
	@ConfigEditorColour
	var colorBlood = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Entrance Room", desc = "Entrance Room color")
	@ConfigEditorColour
	var colorEntrance = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Fairy Room", desc = "Fairy Room color")
	@ConfigEditorColour
	var colorFairy = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Miniboss Room", desc = "miniboss room color")
	@ConfigEditorColour
	var colorMiniboss = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Normal Room", desc = "Normal Room color")
	@ConfigEditorColour
	var colorRoom = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Mimic Room", desc = "mimic room color")
	@ConfigEditorColour
	var colorRoomMimic = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Puzzle Room", desc = "Puzzle Room color")
	@ConfigEditorColour
	var colorPuzzle = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Rare Room", desc = "rare room color")
	@ConfigEditorColour
	var colorRare = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Trap Room", desc = "Trap Room Color")
	@ConfigEditorColour
	var colorTrap = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Unopened Room", desc = "Unopened Room color")
	@ConfigEditorColour
	var colorUnopened = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Show Score", desc = "Shows separate score element.")
	@ConfigEditorBoolean
	var scoreElementEnabled = false

	@ConfigOption(name = "Assume Spirit", desc = "Assume everyone has a legendary spirit pet.")
	@ConfigEditorBoolean
	var scoreAssumeSpirit = true

	@ConfigOption(name = "Minimized Text", desc = "Shortens desc for score elements.")
	@ConfigEditorBoolean
	var scoreMinimizedName = false

	@ConfigOption(name = "Hide in Boss", desc = "Hide in Boss")
	@ConfigEditorBoolean
	var scoreHideInBoss = false

	@ConfigOption(name = "Score", desc = "ScoreTotalScoreSelector")
	@ConfigEditorBoolean
	var scoreTotalScore = false

	@ConfigOption(name = "Secrets", desc = "scoreSecrets")
	@ConfigEditorBoolean
	var scoreSecrets = false

	@ConfigOption(name = "Crypts", desc = "crypts")
	@ConfigEditorBoolean
	var scoreCrypts = false

	@ConfigOption(name = "Mimic", desc = "Mimic")
	@ConfigEditorBoolean
	var scoreMimic = false

	@ConfigOption(name = "Deaths", desc = "Deaths")
	@ConfigEditorBoolean
	var scoreDeaths = false

	@ConfigOption(name = "Puzzles", desc = "scorePuzzles")
	@ConfigEditorBoolean
	var scorePuzzles = false

	@ConfigOption(name = "270 Score Messages", desc = "send message at 270 score")
	@ConfigEditorBoolean
	var scoreMessage270 = false

	@ConfigOption(name = "300 Score Message", desc = "send message at 300 score")
	@ConfigEditorBoolean
	var scoreMessage300 = false

	@ConfigOption(name = "270 Score Title", desc = "Shows score messages as a title notification.")
	@ConfigEditorBoolean
	var scoreTitle270 = false

	@ConfigOption(name = "300 Score Title", desc = "Shows score messages as a title notification.")
	@ConfigEditorBoolean
	var scoreTitle300 = false

	@ConfigOption(name = "270 Message", desc = "Text to send when reaching 270 score")
	@ConfigEditorText
	var message270 = "270 Score"

	@ConfigOption(name = "300 Message", desc = "Text to send when reaching 300 score")
	@ConfigEditorText
	var message300 = "300 Score"

	@ConfigOption(name = "300 Time", desc = "Shows time to reach 300 score.")
	@ConfigEditorBoolean
	var timeTo300 = false

	@ConfigOption(name = "Score", desc = "Score estiate")
	@ConfigEditorBoolean
	var runInformationScore = true

	@ConfigOption(name = "Secrets", desc = "runInformationSecets")
	@ConfigEditorBoolean
	var runInformationSecrets = false

	@ConfigOption(name = "Crypts", desc = "Crypts")
	@ConfigEditorBoolean
	var runInformationCrypts = true

	@ConfigOption(name = "Mimic", desc = "Mimic")
	@ConfigEditorBoolean
	var runInformationMimic = true

	@ConfigOption(name = "Deaths", desc = "Deaths")
	@ConfigEditorBoolean
	var runInformationDeaths = true

	@ConfigOption(name = "Wither Door ESP", desc = "Boxes unopened wither doors.")
	@ConfigEditorBoolean
	var witherDoorESP = false

	@ConfigOption(name = "No Key Color", desc = "No Key Color color")
	@ConfigEditorColour
	var witherDoorNoKeyColor = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Has Key Color", desc = "Has Key Color color")
	@ConfigEditorColour
	var witherDoorKeyColor = ChromaColour(1f, 1f, 1f, 0, 255)

	@ConfigOption(name = "Paul Score", desc = "Assumes paul perk is active to give 10 bonus score.")
	@ConfigEditorBoolean
	var paulBonus = false
}