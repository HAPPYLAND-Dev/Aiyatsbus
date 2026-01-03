package cc.polarastrum.aiyatsbus.module.script.fluxon.function.game

import cc.polarastrum.aiyatsbus.core.compat.AntiGriefChecker
import cc.polarastrum.aiyatsbus.core.compat.GuardItemChecker
import cc.polarastrum.aiyatsbus.module.script.fluxon.relocate.FluxonRelocate
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.java.Export
import org.tabooproject.fluxon.runtime.java.Optional
import taboolib.common.LifeCycle
import taboolib.common.Requires
import taboolib.common.platform.Awake

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.script.fluxon.function.game.FnAntiGriefChecker
 *
 * @author mical
 * @since 2026/1/1 23:49
 */
@Requires(missingClasses = ["!org.tabooproject.fluxon.ParseScript"])
@FluxonRelocate
object FnGuard {

    @Awake(LifeCycle.LOAD)
    fun init() {
        with(FluxonRuntime.getInstance()) {
            registerFunction("aiy:guard", "guard", 0) { FnGuard }
            exportRegistry.registerClass(FnGuard::class.java, "aiy:guard")
        }
    }

    @Export
    fun canBreak(player: Player, location: Location): Boolean {
        return AntiGriefChecker.canBreak(player, location)
    }

    @Export
    fun canDamage(player: Player, entity: Entity): Boolean {
        return AntiGriefChecker.canDamage(player, entity)
    }

    @Export
    fun isGuardItem(item: Item, @Optional player: Player?): Boolean {
        return GuardItemChecker.checkIsGuardItem(item, player)
    }
}