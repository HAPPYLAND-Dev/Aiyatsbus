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

import cc.polarastrum.aiyatsbus.core.toDisplayMode
import cc.polarastrum.aiyatsbus.core.util.isValidJson
import cc.polarastrum.aiyatsbus.module.compat.chat.DisplayReplacer
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.entity.Player
import taboolib.module.nms.NMSItemTag
import java.util.concurrent.ConcurrentHashMap

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.module.compat.chat.display.DisplayReplacerDataComponents
 *
 * @author xiaozhangup
 * @since 2024/8/18 16:43
 */
object DisplayReplacerDataComponents : DisplayReplacer {

    private val gson = GsonComponentSerializer.gson()

    private val jsonParseCache = ConcurrentHashMap<String, JsonObject>(256)
    private val itemDisplayCache = ConcurrentHashMap<String, String>(128)
    private const val MAX_JSON_CACHE_SIZE = 256
    private const val MAX_ITEM_CACHE_SIZE = 128

    @Volatile
    private var cacheCleanupCounter = 0

    private const val HOVER_EVENT_KEY = "hoverEvent"
    private const val ACTION_KEY = "action"
    private const val SHOW_ITEM_ACTION = "show_item"
    private const val CONTENTS_KEY = "contents"

    override fun apply(component: Component, player: Player): Component {
        val json = gson.serialize(component)
        if (!json.isValidJson() || !json.contains(HOVER_EVENT_KEY)) {
            return component
        }

        val jsonObject = getOrParseJson(json) ?: return component

        var modified = false
        applyHoverEvents(jsonObject, player, mutableSetOf()) { modified = true }

        return if (modified) {
            gson.deserialize(jsonObject.toString())
        } else {
            component
        }
    }

    private fun getOrParseJson(json: String): JsonObject? {
        if (++cacheCleanupCounter % 1000 == 0) {
            cleanupCacheIfNeeded()
        }

        return try {
            jsonParseCache.computeIfAbsent(json) {
                JsonParser.parseString(it).asJsonObject
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun applyHoverEvents(
        obj: Any,
        player: Player,
        visited: MutableSet<Any>,
        onModified: () -> Unit
    ) {
        if (obj in visited) return
        visited.add(obj)

        when (obj) {
            is JsonObject -> {
                if (obj.has(HOVER_EVENT_KEY)) {
                    val hoverEvent = obj.getAsJsonObject(HOVER_EVENT_KEY)
                    if (hoverEvent.has(ACTION_KEY) &&
                        hoverEvent.get(ACTION_KEY).asString == SHOW_ITEM_ACTION &&
                        hoverEvent.has(CONTENTS_KEY)) {

                        val contents = hoverEvent.getAsJsonObject(CONTENTS_KEY)
                        if (applyDisplay(contents, hoverEvent, player)) {
                            onModified()
                        }
                    }
                }

                val relevantKeys = obj.keySet().filter { key ->
                    val element = obj.get(key)
                    element.isJsonObject || element.isJsonArray
                }

                relevantKeys.forEach { key ->
                    applyHoverEvents(obj.get(key), player, visited, onModified)
                }
            }

            is JsonArray -> {
                for (i in 0 until obj.size()) {
                    applyHoverEvents(obj.get(i), player, visited, onModified)
                }
            }
        }

        visited.remove(obj)
    }

    private fun applyDisplay(contents: JsonObject, hoverEvent: JsonObject, player: Player): Boolean {
        val json = contents.toString()
        val cachedResult = itemDisplayCache[json]
        if (cachedResult != null) {
            val cachedJsonStructure = JsonParser.parseString(cachedResult).asJsonObject
            hoverEvent.add(CONTENTS_KEY, cachedJsonStructure.getAsJsonObject(HOVER_EVENT_KEY).getAsJsonObject(CONTENTS_KEY))
            return true
        }

        val item = NMSItemTag.instance.fromMinecraftJson(json) ?: return false

        try {
            val newHoverEvent = gson.serialize(
                Component.empty().hoverEvent(item.toDisplayMode(player).asHoverEvent())
            )

            if (itemDisplayCache.size < MAX_ITEM_CACHE_SIZE) {
                itemDisplayCache[json] = newHoverEvent
            }

            val jsonStructure = JsonParser.parseString(newHoverEvent).asJsonObject
            hoverEvent.add(CONTENTS_KEY, jsonStructure.getAsJsonObject(HOVER_EVENT_KEY).getAsJsonObject(CONTENTS_KEY))
            return true
        } catch (_: Exception) {
            return false
        }
    }

    private fun cleanupCacheIfNeeded() {
        if (jsonParseCache.size > MAX_JSON_CACHE_SIZE) {
            val excess = jsonParseCache.size - MAX_JSON_CACHE_SIZE + 50
            val keysToRemove = jsonParseCache.keys.take(excess)
            keysToRemove.forEach { jsonParseCache.remove(it) }
        }

        if (itemDisplayCache.size > MAX_ITEM_CACHE_SIZE) {
            val excess = itemDisplayCache.size - MAX_ITEM_CACHE_SIZE + 25
            val keysToRemove = itemDisplayCache.keys.take(excess)
            keysToRemove.forEach { itemDisplayCache.remove(it) }
        }
    }
}