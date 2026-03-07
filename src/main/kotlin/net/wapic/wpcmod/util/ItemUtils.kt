package net.wapic.wpcmod.util

import com.mojang.authlib.properties.Property
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemLore
import kotlin.jvm.optionals.getOrNull

object ItemUtils {

	val ItemStack.headTexture: String
		get() = get(DataComponents.PROFILE)?.partialProfile()?.properties?.get("textures")?.map(Property::value)?.firstOrNull() ?: ""

	val ItemStack.lore: List<Component> get() = getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines

	val ItemStack.skyblockId: String?
		get() = get(DataComponents.CUSTOM_DATA)?.copyTag()?.getString("id")?.getOrNull()

	fun ItemStack.getSearchName(): String {
		val name = hoverName.string
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
		return `is`(otherItemStack.item) && getSearchName() == otherItemStack.getSearchName()
	}
}