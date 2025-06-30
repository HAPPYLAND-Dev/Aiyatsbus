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
package cc.polarastrum.aiyatsbus.core

/**
 * 标准优先级常量
 *
 * 定义了系统中各个组件的加载和执行优先级。
 * 数值越小优先级越高，确保依赖关系正确的加载顺序。
 *
 * @author mical
 * @since 2024/2/26 23:47
 */
object StandardPriorities {

    /** 稀有度优先级，最高优先级 */
    const val RARITY = 0
    /** 附魔目标优先级 */
    const val TARGET = 1
    /** 事件执行器优先级 */
    const val EVENT_EXECUTORS = 2
    /** 内置触发器优先级 */
    const val INTERNAL_TRIGGERS = 3
    /** 定时器优先级，与内置触发器同级 */
    const val TICKERS = 3
    /** 附魔效果优先级 */
    const val ENCHANTMENT = 4
    /** 冻结注册表优先级 */
    const val FREEZE_REGISTRY = 5
    /** 附魔组优先级 */
    const val GROUP = 6
    /** 限制条件优先级 */
    const val LIMITATIONS = 7
    /** 玩家数据优先级 */
    const val PLAYER_DATA = 8
    /** 菜单系统优先级 */
    const val MENU = 9

    /**
     * 获取数据属性优先级
     *
     * 根据配置 ID 返回对应的优先级值。
     * 只支持品质、附魔组、附魔对象、内置触发器四种类型。
     *
     * @param id 配置项 ID，不区分大小写
     * @return 对应的优先级值，如果 ID 不匹配则返回 -1
     */
    fun getDataProperty(id: String): Int {
        return when (id.lowercase()) {
            "rarity" -> RARITY
            "target" -> TARGET
            "internal_triggers" -> INTERNAL_TRIGGERS
            "group" -> GROUP
            else -> -1
        }
    }
}