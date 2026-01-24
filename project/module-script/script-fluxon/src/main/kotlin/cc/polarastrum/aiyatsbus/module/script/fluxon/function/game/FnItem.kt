package cc.polarastrum.aiyatsbus.module.script.fluxon.function.game

import cc.polarastrum.aiyatsbus.core.belongedTargets
import cc.polarastrum.aiyatsbus.module.script.fluxon.FluxonScriptHandler
import cc.polarastrum.aiyatsbus.module.script.fluxon.relocate.FluxonRelocate
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.java.Export
import taboolib.common.LifeCycle
import taboolib.common.Requires
import taboolib.common.platform.Awake

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.script.fluxon.function.game.FnEntity
 *
 * @author mical
 * @since 2026/1/2 00:09
 */
@Requires(missingClasses = ["!org.tabooproject.fluxon.ParseScript"])
@FluxonRelocate
object FnItem {

    @Awake(LifeCycle.LOAD)
    fun init() {
        FluxonScriptHandler.DEFAULT_PACKAGE_AUTO_IMPORT += "aiy:item"
        with(FluxonRuntime.getInstance()) {
            registerFunction("aiy:item", "item", 0) { FnItem }
            exportRegistry.registerClass(FnItem::class.java, "aiy:item")
        }
    }

    @Export
    fun belongingTargetsId(type: Material): List<String> {
        return type.belongedTargets.map { it.id }
    }

    @Export
    fun belongingTargetsId(item: ItemStack): List<String> {
        return belongingTargetsId(item.type)
    }
}