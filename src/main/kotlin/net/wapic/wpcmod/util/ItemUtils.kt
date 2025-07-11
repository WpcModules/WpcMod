package net.wapic.wpcmod.util

import com.mojang.authlib.properties.Property
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object ItemUtils {

    fun getHeadTexture(stack: ItemStack): String {
        if(!stack.isOf(Items.PLAYER_HEAD) && !stack.contains(DataComponentTypes.PROFILE)) return ""
        val profile = stack.get(DataComponentTypes.PROFILE) ?: return ""
        return profile.properties.get("textures").map(Property::value).first() ?: ""
    }
}