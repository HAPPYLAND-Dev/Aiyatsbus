package cc.polarastrum.aiyatsbus.core

import cc.polarastrum.aiyatsbus.core.data.trigger.ActionType
import org.bukkit.event.player.PlayerEvent
import taboolib.module.configuration.Configuration

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.core.AiyatsbusSkillHandler
 *
 * @author mical
 * @since 2026/1/27 17:19
 */
interface AiyatsbusSkillHandler {

    fun getSettings(): Settings

    fun registerEvents()

    fun unregisterEvents()

    fun handleEvent(e: PlayerEvent, type: ActionType)

    interface Settings {

        /** 配置文件 */
        var conf: Configuration

        /** 是否开启冷却 */
        var enableCooldown: Boolean

        /** 冷却变量名 */
        var cooldownVar: String

        /** 默认触发形式 */
        val action: ActionType

        /** 是否需要下蹲才能触发技能 */
        var shiftNeeded: Boolean

        /** 下蹲时是否不触发技能 */
        var shiftIgnored: Boolean

        /** 冷却减免 */
        val privilege: Map<String, String>
    }
}