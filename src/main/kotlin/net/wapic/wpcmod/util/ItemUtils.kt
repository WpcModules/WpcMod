package net.wapic.wpcmod.util

import com.mojang.authlib.properties.Property
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

object ItemUtils {

    fun ItemStack.getHeadTexture(): String {
        if(!this.isOf(Items.PLAYER_HEAD) && !this.contains(DataComponentTypes.PROFILE)) return ""
        val profile = this.get(DataComponentTypes.PROFILE) ?: return ""
        return profile.properties.get("textures").map(Property::value).first() ?: ""
    }

    fun ItemStack.getLore(): List<Text> {
        return this.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines
    }

    fun ItemStack.getSearchName(): String {
        val name = this.name.string
        if(name == "Enchanted Book") {
            return this.getLore().first().string.trim()
        }
        if(name.startsWith("[Lvl ")) {
            return name.substring("[Lvl ".length)
        }
        return name
    }

    fun ItemStack.isSimilar(otherItemStack: ItemStack): Boolean {
        return this.isOf(otherItemStack.item) && this.getSearchName() == otherItemStack.getSearchName()
    }
}