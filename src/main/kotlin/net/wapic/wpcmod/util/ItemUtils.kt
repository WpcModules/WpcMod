package net.wapic.wpcmod.util

import com.mojang.authlib.properties.Property
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

object ItemUtils {

    fun getHeadTexture(stack: ItemStack): String {
        if(!stack.isOf(Items.PLAYER_HEAD) && !stack.contains(DataComponentTypes.PROFILE)) return ""
        val profile = stack.get(DataComponentTypes.PROFILE) ?: return ""
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
}