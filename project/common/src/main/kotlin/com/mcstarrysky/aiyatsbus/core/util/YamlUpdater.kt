package com.mcstarrysky.aiyatsbus.core.util

import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration

/**
 * module-starrysky
 * com.mcstarrysky.starrysky.config.YamlUpdater
 *
 * @author 米擦亮
 * @since 2023/9/6 20:50
 */
object YamlUpdater {

    fun loadFromFile(path: String, autoUpdate: Boolean = true, updateNodes: List<String> = emptyList(), cache: Configuration? = null): Configuration {
        // 如果配置不存在, 直接释放即可, 并不需要任何检查操作
        if (!newFile(getDataFolder(), path, create = false).exists()) {
            return Configuration.loadFromFile(releaseResourceFile(path))
        }
        val configFile = newFile(getDataFolder(), path)
        val config = YamlConfiguration.loadConfiguration(configFile)

        // 读取 Jar 包内的对应配置文件
        val cachedFile = cache ?: Configuration.loadFromInputStream(javaClass.classLoader.getResourceAsStream(path) ?: return Configuration.loadFromOther(config))

        val updated = mutableListOf<String>()
        read(cachedFile, config, updateNodes, updated, autoUpdate)
        if (updated.isNotEmpty()) {
            config.save(configFile)
        }

        return Configuration.loadFromOther(config)
    }

    private fun read(cache: ConfigurationSection, to: org.bukkit.configuration.ConfigurationSection, updateNodes: List<String>, updated: MutableList<String>, autoUpdate: Boolean) {
        for (key in cache.getKeys(true)) {
            val root = key.split(".").first()
            if (root !in updateNodes && key !in updateNodes) {
                continue
            }
            // 旧版没有, 添加
            if (!to.contains(key)) {
                updated += "$key (+)"
                to[key] = cache[key]
                continue
            }

            // 是否不更新已存在配置, 只补全缺失项
            if (!autoUpdate) continue

            if (cache[key] == null) {
                updated += "$key (${to[key]} -> null)"
                to[key] = null
                continue
            }

            val read = cache[key]
            if (to[key] == read) continue
            to[key] = read
            updated += "$key (${to[key]} -> $read)"
        }
    }
}