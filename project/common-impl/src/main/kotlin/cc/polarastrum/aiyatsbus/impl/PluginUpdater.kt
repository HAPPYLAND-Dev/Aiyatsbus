package cc.polarastrum.aiyatsbus.impl

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.impl.PluginUpdater
 *
 * @author mical
 * @since 2025/1/28 14:11
 */
object PluginUpdater {
}

/**
 * 解析一种格式:
 *
 * updates:
 *   - condition: check &versionId in range 0 to 1
 *     title: "该尼玛更新了！请更新到 Aiyatsbus 1.0.0-pre21 及以上版本"
 *     content:
 *       - "否则你服务器就完了"
 *     timestamp: 1738052189650
 *
 * 该文件挂在阿里云 OSS 上，用于开服时插件联网获取
 */
// data class PluginUpdate()