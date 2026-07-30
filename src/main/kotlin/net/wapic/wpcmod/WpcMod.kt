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
import net.wapic.wpcmod.commands.*
import net.wapic.wpcmod.config.ConfigManager
import net.wapic.wpcmod.config.WpcConfig
import net.wapic.wpcmod.features.chat.*
import net.wapic.wpcmod.features.dev.SkyBlockID
import net.wapic.wpcmod.features.dungeons.*
import net.wapic.wpcmod.features.dungeons.floor7.*
import net.wapic.wpcmod.features.dungeons.funnymap.dungeon.FunnyMap
import net.wapic.wpcmod.features.end.EndESP
import net.wapic.wpcmod.features.entity.*
import net.wapic.wpcmod.features.events.diana.AutoAnswerSphinx
import net.wapic.wpcmod.features.fishing.AutoFish
import net.wapic.wpcmod.features.foraging.ForestNodeESP
import net.wapic.wpcmod.features.galatea.GalateaESP
import net.wapic.wpcmod.features.garden.PestESP
import net.wapic.wpcmod.features.general.CenturyCakeHelper
import net.wapic.wpcmod.features.general.Freecam
import net.wapic.wpcmod.features.general.PreventPlacingItems
import net.wapic.wpcmod.features.general.shortcut.ShortcutHandler
import net.wapic.wpcmod.features.instance.AutoGFS
import net.wapic.wpcmod.features.instance.CancelInteract
import net.wapic.wpcmod.features.inventory.AutoCloseWardrobe
import net.wapic.wpcmod.features.inventory.DyeColor
import net.wapic.wpcmod.features.inventory.ScrollableTooltips
import net.wapic.wpcmod.features.inventory.experiments.AutoExperiments
import net.wapic.wpcmod.features.inventory.experiments.SuperpairsSolver
import net.wapic.wpcmod.features.kuudra.KuudraESP
import net.wapic.wpcmod.features.kuudra.RendAnnounce
import net.wapic.wpcmod.features.mining.ChestESP
import net.wapic.wpcmod.features.mining.CorpseESP
import net.wapic.wpcmod.features.mining.PigeonSwapper
import net.wapic.wpcmod.features.slayer.GummyBearTimer
import net.wapic.wpcmod.hud.HudManager
import net.wapic.wpcmod.listeners.NetworkListener
import net.wapic.wpcmod.util.*
import net.wapic.wpcmod.util.Utils.modIdentifier
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
	val LOGGER: Logger = LoggerFactory.getLogger("WpcMod")

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

	val category: KeyMapping.Category = KeyMapping.Category.register(modIdentifier(MOD_ID))

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
			)

			dispatcher.register(ClientCommands.literal("itistimetofuckingupdate").executes {
				startUpdate()
				return@executes 1
			})

			dispatcher.register(ClientCommands.literal("wpcmod").redirect(mainCommand))
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
		Utils.init()
		SackUtils.init()
		KuudraUtils.init()
		DungeonUtils.init()
		NetworkListener.init()
		EspCache.init()
		HudManager.init()
		TrapperAPI.init()

		// General
		ShortcutHandler.init()
		PreventPlacingItems.init()
		Freecam()
		RatESP.init()
		TagESP.init()
		TrapperESP.init()
		FairySoulESP.init()
		CenturyCakeHelper.init()

		//Experiments
		AutoExperiments.init()
		SuperpairsSolver.init()

		//Instance
		CancelInteract.init()
		AutoGFS.init()

		// Dungeons
		AutoCloseChests.init()
		ScoreCalculation.init()
		DungeonESP.init()
		TickTimers.init()
		TerminalSolver.init()
		MelodyMessage.init()
		InactiveWaypoints.init()
		ArrowAlign.init()
		FunnyMap.init()
		SpiritBearTimer.init()
		DungeonBreaker.init()
		InvincibilityTimer.init()
		EasySuperboom.init()
		LividSolver.init()
		AutoDebuff.init()
		AutoShowExtraStats.init()

		// Kuudra
		KuudraESP.init()
		RendAnnounce.init()

		// Slayers
		GummyBearTimer.init()

		// Galatea
		GalateaESP.init()
		ForestNodeESP.init()

		// End
		EndESP.init()

		// Events

		// Diana
		AutoAnswerSphinx.init()

		// Fishing
		AutoFish.init()
		
		// Mining
		PigeonSwapper.init()
		ChestESP.init()
		CorpseESP.init()

		// Garden
		PestESP.init()

		// Chat
		CompactChat.init()
		SpamFilter.init()
		ChatEmotes.init()
		AutoAcceptPartyInvite.init()
		AutoAcceptTrapper.init()
		QuickMathSolver.init()

		// Inventory
		ScrollableTooltips.init()
		AutoCloseWardrobe.init()
		DyeColor.init()

		// Dev
		SkyBlockID.init()
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
}