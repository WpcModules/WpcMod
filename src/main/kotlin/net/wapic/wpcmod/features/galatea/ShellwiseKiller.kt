package net.wapic.wpcmod.features.galatea

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.player.PlayerInventory
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.mixin.accessors.MinecraftClientAccessor
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.ItemUtils.skyBlockID
import net.wapic.wpcmod.util.MC
import net.wapic.wpcmod.util.Utils.equalsOneOf

object ShellwiseKiller {

	val oneShotBind: KeyBinding = KeyBindingHelper.registerKeyBinding(KeyBinding("one_shot_bind", InputUtil.GLFW_KEY_J, WpcMod.category))

	fun init() {
		ClientTickEvents.END_CLIENT_TICK.register(::onTick)
	}

	private fun onTick(client: MinecraftClient) {
		if(!oneShotBind.wasPressed()) return
		val player = client.player ?: return

		if(!player.mainHandStack.skyBlockID.equalsOneOf("SOUL_WHIP", "FLAMING_FLAY")) return

		WpcMod.coroutineScope.launch {
			val splitterSlot = findInHotbar("FIGSTONE_SPLITTER", player.inventory) ?: return@launch ChatUtils.sendMessage("Unable to find Figstone splitter in hotbar")
			val netSlot = findInHotbar("FISHING_NET", player.inventory) ?: return@launch ChatUtils.sendMessage("Unable to find Fishing net in hotbar")

			MC.runOnThread { (client as MinecraftClientAccessor).doItemUse_WpcMod() }
			delay(50) // 1 tick
			player.inventory.selectedSlot = splitterSlot // TODO: add check to see if in any gui
			delay(300) // 6 ticks idk we'll improve it later
			player.inventory.selectedSlot = netSlot // TODO: add check to see if in any gui
			delay(50)
			MC.runOnThread { (client as MinecraftClientAccessor).doItemUse_WpcMod() }
		}
	}

	fun findInHotbar(skyblockID: String, inventory: PlayerInventory): Int? {
		return inventory.indexOfFirst { it.skyBlockID == skyblockID }.takeIf { it in 0..8 }
	}
}