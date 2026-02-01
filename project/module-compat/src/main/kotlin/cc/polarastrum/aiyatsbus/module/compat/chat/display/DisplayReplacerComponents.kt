/*
 *  Copyright (C) 2022-2024 PolarAstrumLab
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package cc.polarastrum.aiyatsbus.module.compat.chat.display

import cc.polarastrum.aiyatsbus.core.Aiyatsbus
import cc.polarastrum.aiyatsbus.core.toDisplayMode
import cc.polarastrum.aiyatsbus.core.util.isValidJson
import cc.polarastrum.aiyatsbus.module.compat.chat.DisplayReplacer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.module.compat.chat.display.DisplayReplacerDataComponents
 *
 * @author xiaozhangup
 * @since 2024/8/18 16:43
 */
object DisplayReplacerComponents : DisplayReplacer {

    private val gson = GsonComponentSerializer.gson()

    @Suppress("DEPRECATION")
    override fun apply(component: Any, player: Player): Any {
        var json = when (component) {
            is Component -> gson.serialize(component) // Adventure Component
            is String -> component // Json
            else -> Aiyatsbus.api().getMinecraftAPI().getHelper().componentToJson(component) // 大胆假设是 IChatBaseComponent
        }
        if (!json.isValidJson()) { return component }
        if (!json.contains("hover_event") || !json.contains("show_item")) { return component }

        val source = extractShowItemHoverEvents(json)
        if (source.isEmpty()) { return component }
        source.forEach {
            val source = it.toString()
            it.addProperty("DataVersion", Bukkit.getUnsafe().dataVersion)
            val item = Bukkit.getUnsafe().deserializeItemFromJson(it)
            val displayed = Bukkit.getUnsafe().serializeItemAsJson(item.toDisplayMode(player))
            it.remove("DataVersion")
            it.add("components", displayed["components"])
            json = json.replace(source, it.toString())
        }

        return try {
            when (component) {
                is Component -> gson.deserialize(json)
                is String -> json
                else -> Aiyatsbus.api().getMinecraftAPI().getHelper().componentFromJson(json)
            }
        } catch (_: Throwable) {
            return component
        }
    }

    /**
     * 主函数：提取 action 为 show_item 的 hover_event
     */
    fun extractShowItemHoverEvents(jsonString: String?): Set<JsonObject> {
        if (jsonString.isNullOrEmpty()) return emptySet()

        val results = mutableSetOf<JsonObject>()

        try {
            val root = JsonParser.parseString(jsonString)
            recursiveFind(root, results)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        }

        return results
    }

    /**
     * 递归查找函数
     */
    private fun recursiveFind(element: JsonElement, results: MutableSet<JsonObject>) {
        when {
            element.isJsonObject -> {
                val obj = element.asJsonObject

                if (obj.has("hover_event")) {
                    val hoverElement = obj.get("hover_event")

                    if (hoverElement.isJsonObject) {
                        val hoverObj = hoverElement.asJsonObject
                        val action = hoverObj.get("action")?.asString

                        if (action == "show_item") {
                            results.add(hoverObj)
                        }
                    }
                }

                for ((_, value) in obj.entrySet()) {
                    recursiveFind(value, results)
                }
            }

            element.isJsonArray -> {
                element.asJsonArray.forEach { item ->
                    recursiveFind(item, results)
                }
            }
        }
    }
}