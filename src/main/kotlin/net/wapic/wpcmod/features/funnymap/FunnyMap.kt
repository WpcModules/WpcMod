package net.wapic.wpcmod.features.funnymap

import net.wapic.wpcmod.features.funnymap.dungeon.Dungeon
import net.wapic.wpcmod.features.funnymap.dungeon.RunInformation
import net.wapic.wpcmod.features.funnymap.dungeon.WitherDoorESP
import net.wapic.wpcmod.features.funnymap.ui.MapElement

object FunnyMap {

	fun init() {
		WitherDoorESP.init()
		Dungeon.init()
		RunInformation.init()
		MapElement.init()
	}
}
