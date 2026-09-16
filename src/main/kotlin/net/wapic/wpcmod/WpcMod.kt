package net.wapic.wpcmod

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import moe.nea.libautoupdate.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.metadata.ModMetadata
import net.minecraft.ChatFormatting
import net.minecraft.client.KeyMapping
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.wapic.wpcmod.commands.*
import net.wapic.wpcmod.config.ConfigManager
import net.wapic.wpcmod.config.WpcConfig
import net.wapic.wpcmod.features.general.CenturyCakeHelper
import net.wapic.wpcmod.features.general.Freecam
import net.wapic.wpcmod.generated.runStartupFunctions
import net.wapic.wpcmod.hud.HudManager
import net.wapic.wpcmod.util.ChatUtils
import net.wapic.wpcmod.util.render.WpcModRenderSystem
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
	val LOGGER: Logger = LoggerFactory.getLogger(WpcMod::class.java)

	val globalJob = Job()
	val coroutineScope = CoroutineScope(EmptyCoroutineContext + CoroutineName("WpcMod") + SupervisorJob(globalJob))

	val updateContext = UpdateContext(
		UpdateSource.mavenSource(
			"https://maven.wapic.net/releases",
			"net.wapic.$MOD_ID",
			"$MOD_ID-mc-${FabricLoader.getInstance().rawGameVersion}"
		),
		UpdateTarget.deleteAndSaveInTheSameFolder(WpcMod::class.java),
		CurrentVersion.ofTag(version.friendlyString),
		MOD_ID
	)
	private var potentialUpdate: PotentialUpdate? = null

	val category: KeyMapping.Category = KeyMapping.Category.register(Identifier(MOD_ID))

	override fun onInitialize() {
		ConfigManager.firstLoad()

		ClientCommandRegistrationCallback.EVENT.register { dispatcher, registryAccess ->
			val mainCommand = dispatcher.register(
				WpcModCommand.getCommand()
					.then(UpdateCommand.getCommand())
					.then(ShortcutsCommand.getCommand())
					.then(TagCommand.getCommand())
					.then(FreecamCommand.getCommand())
					.then(TermSimCommand.getCommand())
					.then(FunnyMapCommands.getCommand())
					.then(HudEditorCommand.getCommand())
					.then(GFSCommand.getCommand())
					.then(SimulateCommand.getCommand())
			)

			dispatcher.register(ClientCommands.literal("itistimetofuckingupdate").executes {
				startUpdate()
				return@executes 1
			})

			dispatcher.register(ClientCommands.literal("wpcmod").executes {
				ConfigManager.openConfig(it.source.client)
				return@executes 1
			}.redirect(mainCommand))
		}

		ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
			if (!updateNotified) {
				updateNotified = true
				checkUpdate()
			}
		}

		ClientLifecycleEvents.CLIENT_STOPPING.register {
			ConfigManager.saveConfig()
			HudManager.saveLocations()
			CenturyCakeHelper.saveTimes()
			globalJob.cancel()
		}

		/* Initialize */
		Freecam()

		runStartupFunctions()

		WpcModRenderSystem()

		LOGGER.info("WpcMod Initialized")
	}

	fun checkUpdate() {
		updateContext.checkUpdate("upstream").thenAcceptAsync {
			potentialUpdate = it
			if (it.isUpdateAvailable) {
				ChatUtils.sendMessage(
					"Update found: §e${updateContext.currentVersion.display()}§r -> §e${potentialUpdate?.update?.versionName}§r Click here to update",
					Style.EMPTY.withHoverEvent(
						HoverEvent.ShowText(Component.nullToEmpty("Click to update"))
					).withClickEvent(
						ClickEvent.RunCommand("/itistimetofuckingupdate")
					).withColor(ChatFormatting.WHITE)
				)

				LOGGER.info("Update Found: {}, Version: {}", it.isUpdateAvailable, it.update.versionName)
			}
		}
	}

	fun startUpdate() {
		val potentialUpdate = potentialUpdate ?: return ChatUtils.sendMessage("No updates found!")

		LOGGER.info("Starting update...")
		ChatUtils.sendMessage("Starting update...")

		potentialUpdate.launchUpdate().whenComplete { void, throwable ->
			LOGGER.info("${potentialUpdate.update.versionName} Update complete!")
			ChatUtils.sendMessage("Update complete! the updates will be applied on next restart.")
		}
	}

	fun Identifier(path: String): Identifier = Identifier.fromNamespaceAndPath(MOD_ID, path)
}