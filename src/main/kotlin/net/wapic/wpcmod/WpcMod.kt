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
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.wapic.wpcmod.config.ConfigManager
import net.wapic.wpcmod.features.galatea.GalateaESP
import net.wapic.wpcmod.features.general.ArmorSwapper
import net.wapic.wpcmod.features.general.AutoExperiments
import net.wapic.wpcmod.features.general.shortcut.ShortcutHandler
import net.wapic.wpcmod.features.general.shortcut.ShortcutScreen
import net.wapic.wpcmod.features.kuudra.KuudraAutoGFS
import net.wapic.wpcmod.util.Utils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.EmptyCoroutineContext

object WpcMod : ModInitializer {
	const val MOD_ID = "wpcmod"
	const val PREFIX = "§b[WpcMod]§r:"

	private val metadata: ModMetadata by lazy {
		FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().metadata
	}
	val version: Version by lazy { metadata.version }

    val logger: Logger = LoggerFactory.getLogger("WpcMod")

	val globalJob = Job()
	val coroutineScope = CoroutineScope(EmptyCoroutineContext + CoroutineName("WpcMod") + SupervisorJob(globalJob))

	override fun onInitialize() {
		ConfigManager.createConfig()

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
						IMinecraft.instance.openWrappedScreen(ConfigManager.getEditorInstance())
					}
					0
				}.then(ClientCommandManager.literal("binds").executes {
					MinecraftClient.getInstance().send {
						MinecraftClient.getInstance().setScreen(ShortcutScreen())
					}
					0
				}).then(ClientCommandManager.literal("update").executes {
					if(potentialUpdate.isUpdateAvailable) {
						MinecraftClient.getInstance().inGameHud.chatHud.addMessage(
							Text.of("$PREFIX Updating from ${updateContext.currentVersion} to ${potentialUpdate.update.versionName}")
						)
						potentialUpdate.launchUpdate()
					} else {
						MinecraftClient.getInstance().inGameHud.chatHud.addMessage(Text.of("$PREFIX No Updates Available"))
					}
					0
				})
			)
		}

		ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
			if(!potentialUpdate.isUpdateAvailable) return@register

			MinecraftClient.getInstance().inGameHud.chatHud.addMessage(
				Text.literal("$PREFIX Update found: ${potentialUpdate.update.versionName}").append(
					Text.literal("Click here to Update!").setStyle(
						Style.EMPTY.withColor(Formatting.AQUA).withColor(Formatting.UNDERLINE).withClickEvent(ClickEvent.RunCommand("/wpcmod update"))
					)
				)
			)
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
		AutoExperiments()
		ArmorSwapper()

		// Galatea
		GalateaESP()

		// Kuudra
		KuudraAutoGFS()
	}
}