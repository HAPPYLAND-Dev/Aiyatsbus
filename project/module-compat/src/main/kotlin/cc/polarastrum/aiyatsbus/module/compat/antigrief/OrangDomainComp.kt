package cc.polarastrum.aiyatsbus.module.compat.antigrief

import cc.polarastrum.aiyatsbus.core.compat.AntiGrief
import cc.polarastrum.aiyatsbus.core.compat.AntiGriefChecker
import me.xiaozhangup.domain.utils.getPoly
import org.bukkit.Location
import org.bukkit.entity.Entity
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