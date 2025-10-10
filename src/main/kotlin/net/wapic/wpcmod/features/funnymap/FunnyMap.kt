package net.wapic.wpcmod.features.funnymap

import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.features.funnymap.features.dungeon.Dungeon
import net.wapic.wpcmod.features.funnymap.features.dungeon.RunInformation
import net.wapic.wpcmod.features.funnymap.features.dungeon.WitherDoorESP
import net.wapic.wpcmod.features.funnymap.ui.MapElement

object FunnyMap {
	val mc: MinecraftClient = MinecraftClient.getInstance()

	fun init() {
		WitherDoorESP.init()
		Dungeon.init()
		RunInformation.init()
		MapElement.init()
	}
}
