package net.wapic.wpcmod.util

import com.mojang.authlib.properties.Property
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import kotlin.jvm.optionals.getOrNull

object ItemUtils {

	val ItemStack.headTexture: String
		get() = get(DataComponentTypes.PROFILE)?.properties?.get("textures")?.map(Property::value)?.firstOrNull() ?: ""

	val ItemStack.lore: List<Text> get() = getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines

	val ItemStack.skyBlockID: String?
		get() = get(DataComponentTypes.CUSTOM_DATA)?.copyNbt()?.getString("id")?.getOrNull()

	fun ItemStack.getSearchName(): String {
		val name = name.string
		if (name == "Enchanted Book") {
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
		return isOf(otherItemStack.item) && getSearchName() == otherItemStack.getSearchName()
	}
}