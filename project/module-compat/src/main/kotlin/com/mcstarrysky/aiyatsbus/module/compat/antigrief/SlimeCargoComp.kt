package com.mcstarrysky.aiyatsbus.module.compat.antigrief

import com.mcstarrysky.aiyatsbus.core.compat.AntiGrief
import com.mcstarrysky.aiyatsbus.core.compat.AntiGriefChecker
import me.xiaozhangup.slimecargo.SlimeCargoNext.slimeProtect
import me.xiaozhangup.slimecargo.objects.enums.ProtectType
import me.xiaozhangup.slimecargo.protect.SlimeProtect
import org.bukkit.Location
import org.bukkit.entity.Animals
import org.bukkit.entity.Entity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

class SlimeCargoComp : AntiGrief {
    override fun canPlace(player: Player, location: Location): Boolean {
        return slimeProtect.hasPermission(
            ProtectType.BUILD,
            player,
            location = location
        ) != SlimeProtect.Result.DENY
    }

    override fun canBreak(player: Player, location: Location): Boolean {
        return slimeProtect.hasPermission(
            ProtectType.BREAK,
            player,
            location = location,
        ) != SlimeProtect.Result.DENY
    }

    override fun canInteract(player: Player, location: Location): Boolean {
        return slimeProtect.hasPermission(
            ProtectType.INTERACT_BLOCK,
            player,
            location = location
        ) != SlimeProtect.Result.DENY
    }

    override fun canInteractEntity(player: Player, entity: Entity): Boolean {
        return slimeProtect.hasPermission(
            ProtectType.INTERACT_ENTITY,
            player,
            entity = entity
        ) != SlimeProtect.Result.DENY
    }

    override fun canDamage(player: Player, entity: Entity): Boolean {
        return slimeProtect.hasPermission(
            when (entity) {
                is Animals -> ProtectType.HUNTER
                is Monster -> ProtectType.ATTACK
                else -> ProtectType.PVP
            },
            player,
            entity = entity
        ) != SlimeProtect.Result.DENY
    }

    override fun getAntiGriefPluginName(): String {
        return "SlimeCargoNext"
    }

    companion object {

        @Awake(LifeCycle.ACTIVE)
        fun init() {
            AntiGriefChecker.registerNewCompatibility(SlimeCargoComp())
        }
    }
}