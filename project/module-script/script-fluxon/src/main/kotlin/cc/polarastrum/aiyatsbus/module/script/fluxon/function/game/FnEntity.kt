package cc.polarastrum.aiyatsbus.module.script.fluxon.function.game

import cc.polarastrum.aiyatsbus.core.util.isBehind
import cc.polarastrum.aiyatsbus.core.util.realDamage
import cc.polarastrum.aiyatsbus.module.script.fluxon.FluxonScriptHandler
import cc.polarastrum.aiyatsbus.module.script.fluxon.relocate.FluxonRelocate
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.java.Export
import org.tabooproject.fluxon.runtime.java.Optional
import taboolib.common.LifeCycle
import taboolib.common.Requires
import taboolib.common.platform.Awake
import taboolib.module.nms.getI18nName

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.script.fluxon.function.game.FnEntity
 *
 * @author mical
 * @since 2026/1/2 00:09
 */
@Requires(missingClasses = ["!org.tabooproject.fluxon.ParseScript"])
@FluxonRelocate
object FnEntity {

    @Awake(LifeCycle.LOAD)
    fun init() {
        FluxonScriptHandler.DEFAULT_PACKAGE_AUTO_IMPORT += "aiy:entity"
        with(FluxonRuntime.getInstance()) {
            registerFunction("aiy:entity", "entity", 0) { FnEntity }
            exportRegistry.registerClass(FnEntity::class.java, "aiy:entity")
        }
    }

    @Export
    fun spawnEntity(entityType: String, location: Location) {
        location.world.spawnEntity(location, EntityType.valueOf(entityType.uppercase()))
    }

    @Export
    fun realDamage(entity: LivingEntity, damage: Double, @Optional by: Entity?) {
        entity.realDamage(damage, by)
    }

    @Export
    fun entityName(entity: Entity, @Optional player: Player?): String {
        return if (entity is Player) entity.name else entity.customName ?: entity.getI18nName(player)
    }

    @Export
    fun removeEntity(entity: Entity) {
        entity.remove()
    }

    @Export
    fun isBehind(entity1: LivingEntity, entity2: LivingEntity): Boolean {
        return entity1.isBehind(entity2)
    }
}