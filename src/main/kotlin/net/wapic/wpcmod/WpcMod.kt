package net.wapic.wpcmod

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
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.wapic.wpcmod.commands.*
import net.wapic.wpcmod.config.ConfigManager
import net.wapic.wpcmod.config.WpcConfig
import net.wapic.wpcmod.features.AutoGFS
import net.wapic.wpcmod.features.chat.SpamFilter
import net.wapic.wpcmod.features.dev.SkyBlockID
import net.wapic.wpcmod.features.dungeons.AutoCloseChests
import net.wapic.wpcmod.features.end.EndESP
import net.wapic.wpcmod.features.galatea.GalateaESP
import net.wapic.wpcmod.features.general.*
import net.wapic.wpcmod.features.general.experiments.AutoExperiments
import net.wapic.wpcmod.features.general.experiments.SuperpairsSolver
import net.wapic.wpcmod.features.general.shortcut.ShortcutHandler
import net.wapic.wpcmod.features.kuudra.KuudraDisplay
import net.wapic.wpcmod.features.kuudra.KuudraESP
import net.wapic.wpcmod.listeners.ChatListener
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.KuudraUtils
import net.wapic.wpcmod.util.SackUtils
import net.wapic.wpcmod.util.Utils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.coroutines.EmptyCoroutineContext

object WpcMod : ModInitializer {

	const val MOD_ID = "wpcmod"
	private val metadata: ModMetadata by lazy {
		FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().metadata
	}
	private var updateNotified = false

	@JvmField
	var config: WpcConfig = WpcConfig()
	val configDir = File("config/wpcmod")

	val version: Version by lazy { metadata.version }
	val logger: Logger = LoggerFactory.getLogger("WpcMod")

	val globalJob = Job()
	val coroutineScope = CoroutineScope(EmptyCoroutineContext + CoroutineName("WpcMod") + SupervisorJob(globalJob))

	val updateContext = UpdateContext(
		UpdateSource.mavenSource("https://maven.wapic.net/releases", "net.wapic.$MOD_ID", MOD_ID),
		UpdateTarget.deleteAndSaveInTheSameFolder(WpcMod::class.java),
		CurrentVersion.ofTag(version.friendlyString),
		MOD_ID
	)

	override fun onInitialize() {
		ConfigManager.firstLoad()

		val future = updateContext.checkUpdate("upstream")
		val potentialUpdate = future.get()

		ClientCommandRegistrationCallback.EVENT.register { dispatcher, registryAccess ->
			val mainCommand = dispatcher.register(
				WpcModCommand.getCommand()
					.then(UpdateCommand.getCommand())
					.then(ShortcutsCommand.getCommand())
					.then(TagCommand.getCommand())
			)

			dispatcher.register(ClientCommandManager.literal("wpcmod").redirect(mainCommand))
		}

		ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
			if ((potentialUpdate.isUpdateAvailable || FabricLoader.getInstance().isDevelopmentEnvironment) && !updateNotified) {
				updateNotified = true
				ChatUtils.sendMessage(
					"Update found: §e${updateContext.currentVersion.display()}§r. -> §e${potentialUpdate.update.versionName}§r Click here to update",
					Style.EMPTY.withHoverEvent(
						HoverEvent.ShowText(Text.of("Click to update"))
					).withClickEvent(
						ClickEvent.RunCommand("/wpcmod update")
					)
				)
			}
		}

		ClientLifecycleEvents.CLIENT_STOPPING.register {
			ConfigManager.saveConfig()
			globalJob.cancel()
		}

		/* Initialize */
		Utils.init()
		SackUtils.init()
		KuudraUtils.init()
		ChatListener()

		// General
		ShortcutHandler()
		ArmorSwapper()
		PreventPlacingItems()
		DiscardHighlighter()
		DisableFrontCamera()
		ScrollableTooltips()

		//Experiments
		AutoExperiments()
		SuperpairsSolver()

		// Dungeons
		AutoCloseChests()

		// Kuudra
		KuudraDisplay()
		KuudraESP()

		// Galatea
		GalateaESP()

		// End
		EndESP()

		// Chat
		SpamFilter()

		// Multi Category
		AutoGFS()

		// Dev
		SkyBlockID()
	}
}