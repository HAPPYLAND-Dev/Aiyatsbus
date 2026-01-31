package cc.polarastrum.aiyatsbus.impl.nms12111

import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack

@Suppress("DEPRECATION")
object AiyatsbusBukkit12111Unsafe {

    fun deserializeItemFromJson(json: JsonObject): ItemStack {
        return Bukkit.getUnsafe().deserializeItemFromJson(json)
    }

    fun serializeItemAsJson(item: ItemStack): JsonObject {
        return Bukkit.getUnsafe().serializeItemAsJson(item)
    }

}