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
package cc.polarastrum.aiyatsbus.core.data.trigger.event

import cc.polarastrum.aiyatsbus.core.util.*
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

/**
 * 事件解析器类
 *
 * 定义事件数据的解析逻辑，包含实体解析、事件解析和物品解析功能。
 * 用于从 Bukkit 事件中提取相关的实体、事件和物品信息。
 *
 * @param T 事件类型
 * @author mical
 * @since 2024/7/18 00:58
 */
data class EventResolver<in T : Event>(
    /** 实体解析器，从事件中提取生物实体信息 */
    val entityResolver: Function2To2<T, String?, LivingEntity?, Boolean>,
    /** 事件解析器，处理事件本身的逻辑 */
    val eventResolver: Function1<T> = Function1 { _ -> },
    /** 物品解析器，从事件中提取物品信息 */
    val itemResolver: Function4To2<T, String?, LivingEntity, EquipmentSlot?, ItemStack?, Boolean> = Function4To2 { _, _, entity, slot ->
        defaultItemResolver(entity, slot)
    }
) {

    companion object {

        fun defaultItemResolver(entity: LivingEntity, slot: EquipmentSlot?): Pair1<ItemStack?, Boolean> {
            if (slot == null) return null to1 false
            return try {
                entity.equipment?.getItem(slot) to1 true
            } catch (_: Throwable) {
                // 离谱的低版本报错:
                // java.lang.NullPointerException: player.inventory.getItem(slot) must not be null
                return null to1 false
            }
        }
    }
}