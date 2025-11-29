package net.wapic.wpcmod.features.inventory.inventorybinds

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.input.KeyInput
import net.minecraft.client.util.InputUtil
import net.minecraft.screen.slot.SlotActionType
import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.events.GuiEvents
import net.wapic.wpcmod.util.MC
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.io.File

object InventoryBindsHandler {
	private val saveFile = File(WpcMod.configDir, "inventory-binds.json")
	private val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()
	val loadedInventoryBinds = mutableMapOf<String, MutableList<InventoryBind>>()

	fun init() {
		GuiEvents.KEY_PRESSED.register(::onKeyPressed)

		ClientLifecycleEvents.CLIENT_STARTED.register {
			loadInventoryBinds()
		}

		ClientLifecycleEvents.CLIENT_STOPPING.register {
			saveInventoryBinds()
		}
	}

	private fun onKeyPressed(input: KeyInput, cir: CallbackInfoReturnable<Boolean>) {
		val title = MC.screenName ?: return
		val screenHandler = MC.instance.player?.currentScreenHandler ?: return

		loadedInventoryBinds.forEach { (inventory, binds) ->
			if (inventory != title) return@forEach
			val bind = binds.find { it.key.code == input.keycode } ?: return@forEach
			println("$title ${bind.slot}")
			MC.interactionManager?.clickSlot(
				screenHandler.syncId,
				bind.slot,
				InputUtil.GLFW_MOUSE_BUTTON_LEFT,
				SlotActionType.PICKUP,
				MC.player
			)
		}
	}

	fun loadInventoryBinds() {
		if (saveFile.exists()) {
			try {
				WpcMod.logger.info("Loading Inventory Binds")
				//loadedInventoryBinds.addAll(gson.fromJson(saveFile.reader(), Map<String, List<InventoryBind>>::class.java).toList())
			} catch (e: Throwable) {
				WpcMod.logger.error("Failed to read inventory-binds", e)
				val backup = saveFile.resolveSibling("inventory-binds-failed.json")
				try {
					WpcMod.logger.warn("Creating a backup of old file and loading default", e)
					saveFile.copyTo(backup)
				} catch (e: Exception) {
					WpcMod.logger.error("Failed to backup inventory-binds", e)
				}
			}
		}
	}

	fun saveInventoryBinds() {
		try {
			WpcMod.logger.info("Saving Inventory Binds")
			saveFile.parentFile.mkdirs()
			saveFile.createNewFile()
			saveFile.writeText(gson.toJson(loadedInventoryBinds))
		} catch (e: Exception) {
			WpcMod.logger.error("Failed to save Inventory Binds", e)
			throw e
		}
	}
}