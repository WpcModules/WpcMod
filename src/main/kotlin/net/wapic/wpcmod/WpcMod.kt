package net.wapic.wpcmod

import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.managed.ManagedConfig
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.MinecraftClient
import net.wapic.wpcmod.config.WpcConfig
import net.wapic.wpcmod.galatea.GalateaESP
import net.wapic.wpcmod.general.shortcut.ShortcutHandler
import net.wapic.wpcmod.general.shortcut.ShortcutScreen
import net.wapic.wpcmod.kuudra.Kuudra
import net.wapic.wpcmod.util.Utils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.coroutines.EmptyCoroutineContext

object WpcMod : ModInitializer {
    val logger: Logger = LoggerFactory.getLogger("wpcmod")
	lateinit var config: ManagedConfig<WpcConfig>

	val globalJob = Job()
	val coroutineScope = CoroutineScope(EmptyCoroutineContext + CoroutineName("WpcMod") + SupervisorJob(globalJob))

	override fun onInitialize() {
		logger.info("Hello Fabric world!")

		config = ManagedConfig.create(File("config/wpcmod/config.json"), WpcConfig::class.java)

		ClientCommandRegistrationCallback.EVENT.register {
			dispatcher, registryAccess -> dispatcher.register (
				ClientCommandManager.literal("wpcmod").executes { context ->
					MinecraftClient.getInstance().send {
						val editor = config.getEditor()
						IMinecraft.instance.openWrappedScreen(editor)
					}
					0
				}.then(ClientCommandManager.literal("binds").executes {
					MinecraftClient.getInstance().send {
						MinecraftClient.getInstance().setScreen(ShortcutScreen())
					}
					0
				})
			)
		}

		ClientLifecycleEvents.CLIENT_STOPPING.register {
			config.instance.saveNow()
			globalJob.cancel()
		}

		Utils.init()
		ShortcutHandler.init()
		GalateaESP.init()
		Kuudra.init()
	}
}