/*
 * This file is part of EcoEnchants, licensed under the GPL-3.0 License.
 *
 *  Copyright (C) 2024 Auxilor
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

package cc.polarastrum.aiyatsbus.module.compat.antigrief

import cc.polarastrum.aiyatsbus.core.AiyatsbusSettings
import cc.polarastrum.aiyatsbus.core.compat.AntiGrief
import cc.polarastrum.aiyatsbus.core.compat.AntiGriefChecker
import nl.rutgerkok.blocklocker.BlockLockerAPIv2
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.compat.antigrief.BlockLockerComp
 *
 * @author mical
 * @since 2025/2/14 14:55
 */
class BlockLockerComp : AntiGrief {

    override fun canPlace(player: Player, location: Location): Boolean {
        return BlockLockerAPIv2.isAllowed(player, location.block, AiyatsbusSettings.antiGriefIgnoreOp)
    }

    override fun canBreak(player: Player, location: Location): Boolean {
        return BlockLockerAPIv2.isAllowed(player, location.block, AiyatsbusSettings.antiGriefIgnoreOp)
    }

    override fun canInteract(player: Player, location: Location): Boolean {
        return BlockLockerAPIv2.isAllowed(player, location.block, AiyatsbusSettings.antiGriefIgnoreOp)
    }

    override fun canInteractEntity(player: Player, entity: Entity): Boolean {
        return true
    }

    override fun canDamage(player: Player, entity: Entity): Boolean {
        return true
    }

    override fun getAntiGriefPluginName(): String {
        return "BlockLocker"
    }

    companion object {

        @Awake(LifeCycle.ACTIVE)
        fun init() {
            AntiGriefChecker.registerNewCompatibility(BlockLockerComp())
        }
    }
}