package net.wapic.wpcmod.util

import com.mojang.authlib.properties.Property
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import kotlin.jvm.optionals.getOrNull

object ItemUtils {

	fun ItemStack.getHeadTexture(): String {
		if (!this.isOf(Items.PLAYER_HEAD) && !this.contains(DataComponentTypes.PROFILE)) return ""
		val profile = this.get(DataComponentTypes.PROFILE) ?: return ""
		return profile.properties.get("textures").map(Property::value).first() ?: ""
	}

	fun ItemStack.getLore(): List<Text> {
		return this.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines
	}

	fun ItemStack.getSearchName(): String {
		val name = name.string
		if (name == "Enchanted Book") {
			val lore = this.getLore()
			if (lore.first().string.trim().contains("Rare Book!", ignoreCase = true)) {
				return lore[2].string.trim()
			}
			return lore.first().string.trim()
		}
		if (name.startsWith("[Lvl ")) {
			return name.replace("\\[Lvl \\d+] ".toRegex(), "")
		}
		return name
	}

	fun ItemStack.isSimilar(otherItemStack: ItemStack): Boolean {
		return this.isOf(otherItemStack.item) && this.getSearchName() == otherItemStack.getSearchName()
	}

	fun ItemStack.getSkyBlockID(): String? {
		val nbt = this.get(DataComponentTypes.CUSTOM_DATA)?.copyNbt() ?: return null
		return nbt.getString("id").getOrNull()
	}
}