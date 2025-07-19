package net.wapic.wpcmod

import io.github.notenoughupdates.moulconfig.common.IMinecraft
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import moe.nea.libautoupdate.CurrentVersion
import moe.nea.libautoupdate.UpdateContext
import moe.nea.libautoupdate.UpdateSource
import moe.nea.libautoupdate.UpdateTarget
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.metadata.ModMetadata
import net.minecraft.client.MinecraftClient
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.wapic.wpcmod.config.ConfigManager
import net.wapic.wpcmod.config.WpcConfig
import net.wapic.wpcmod.features.dungeons.AutoCloseChests
import net.wapic.wpcmod.features.galatea.GalateaESP
import net.wapic.wpcmod.features.general.ArmorSwapper
import net.wapic.wpcmod.features.general.DiscardHighlighter
import net.wapic.wpcmod.features.general.PreventPlacingItems
import net.wapic.wpcmod.features.general.experiments.AutoExperiments
import net.wapic.wpcmod.features.general.experiments.SuperpairsSolver
import net.wapic.wpcmod.features.general.shortcut.ShortcutHandler
import net.wapic.wpcmod.features.general.shortcut.ShortcutScreen
import net.wapic.wpcmod.features.kuudra.KuudraAutoGFS
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.Utils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.EmptyCoroutineContext

object WpcMod : ModInitializer {
	private const val MOD_ID = "wpcmod"
	private val metadata: ModMetadata by lazy {
		FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().metadata
	}
	private var updateNotified = false

	@JvmField
	var config: WpcConfig = WpcConfig()

	val version: Version by lazy { metadata.version }
    val logger: Logger = LoggerFactory.getLogger("WpcMod")

	val globalJob = Job()
	val coroutineScope = CoroutineScope(EmptyCoroutineContext + CoroutineName("WpcMod") + SupervisorJob(globalJob))

	override fun onInitialize() {
		ConfigManager.firstLoad()

		val updateContext = UpdateContext(
			UpdateSource.mavenSource("https://maven.wapic.net/releases", "net.wapic.$MOD_ID", MOD_ID),
			UpdateTarget.deleteAndSaveInTheSameFolder(WpcMod::class.java),
			CurrentVersion.ofTag(version.friendlyString),
			MOD_ID
		)

		val future = updateContext.checkUpdate("upstream")
		val potentialUpdate = future.get()

		ClientCommandRegistrationCallback.EVENT.register {
			dispatcher, registryAccess -> dispatcher.register (
				ClientCommandManager.literal("wpcmod").executes { context ->
					MinecraftClient.getInstance().send {
						IMinecraft.instance.openWrappedScreen(ConfigManager.getEditor())
					}
					0
				}.then(ClientCommandManager.literal("binds").executes {
					MinecraftClient.getInstance().send {
						MinecraftClient.getInstance().setScreen(ShortcutScreen())
					}
					0
				}).then(ClientCommandManager.literal("update").executes {
					if(potentialUpdate.isUpdateAvailable) {
						ChatUtils.sendMessage("Launching update...")
						potentialUpdate.launchUpdate().thenRun {
							ChatUtils.sendMessage("Download complete! Update will apply after you restart")
						}
					} else {
						ChatUtils.sendMessage("No Updates Available")
					}
					0
				})
			)
		}

		ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
			if((potentialUpdate.isUpdateAvailable || FabricLoader.getInstance().isDevelopmentEnvironment) && !updateNotified) {
				updateNotified = true
				ChatUtils.sendMessage("Update found: §e${updateContext.currentVersion.display()}§r. -> §e${potentialUpdate.update.versionName}§r Click here to update", Style.EMPTY.withHoverEvent(
					HoverEvent.ShowText(Text.of("Click to update"))
				).withClickEvent(
					ClickEvent.RunCommand("/wpcmod update")
				))
			}
		}

		ClientLifecycleEvents.CLIENT_STOPPING.register {
			ConfigManager.saveConfig()
			globalJob.cancel()
		}

		/* Initialize Helpers */
		Utils.init()

		/* Initialize features */
		// General
		ShortcutHandler()
		ArmorSwapper()
		PreventPlacingItems()
		DiscardHighlighter()

		//Experiments
		AutoExperiments()
		SuperpairsSolver()

		// Dungeons
		AutoCloseChests()
		DiscardHighlighter()

		// Galatea
		GalateaESP()

		// Kuudra
		KuudraAutoGFS()
	}
}