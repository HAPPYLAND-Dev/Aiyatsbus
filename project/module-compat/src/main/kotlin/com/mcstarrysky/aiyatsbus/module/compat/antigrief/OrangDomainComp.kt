package com.mcstarrysky.aiyatsbus.module.compat.antigrief

import com.mcstarrysky.aiyatsbus.core.compat.AntiGrief
import com.mcstarrysky.aiyatsbus.core.compat.AntiGriefChecker
import me.xiaozhangup.domain.utils.getPoly
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

class OrangDomainComp : AntiGrief {
    override fun canPlace(player: Player, location: Location): Boolean {
        val poly = location.getPoly() ?: return true
        return poly.hasPermission("build", player.name)
    }

    override fun canBreak(player: Player, location: Location): Boolean {
        val poly = location.getPoly() ?: return true
        return poly.hasPermission("build", player.name) && poly.isDestructible(location.block.type)
    }

    override fun canInteract(player: Player, location: Location): Boolean {
        val poly = location.getPoly() ?: return true
        return poly.hasPermission("interact", player.name)
    }

    override fun canInteractEntity(player: Player, entity: Entity): Boolean {
        val poly = entity.location.getPoly() ?: return true
        return poly.hasPermission("interact", player.name)
    }

    override fun canDamage(player: Player, entity: Entity): Boolean {
        val poly = entity.location.getPoly() ?: return true
        return poly.hasPermission("pvp", player.name)
    }

    override fun getAntiGriefPluginName(): String {
        return "OrangDomain"
    }

    companion object {

        @Awake(LifeCycle.ACTIVE)
        fun init() {
            AntiGriefChecker.registerNewCompatibility(OrangDomainComp())
        }
    }
}