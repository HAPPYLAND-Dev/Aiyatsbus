package cc.polarastrum.aiyatsbus.module.script.fluxon.function.game

import cc.polarastrum.aiyatsbus.module.script.fluxon.relocate.FluxonRelocate
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.java.Export
import org.tabooproject.fluxon.runtime.java.Optional
import taboolib.common.LifeCycle
import taboolib.common.Requires
import taboolib.common.platform.Awake

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.script.fluxon.function.game.FnBlock
 *
 * @author mical
 * @since 2026/1/1 23:56
 */
@Requires(missingClasses = ["!org.tabooproject.fluxon.ParseScript"])
@FluxonRelocate
object FnBlock {

    @Awake(LifeCycle.LOAD)
    fun init() {
        with(FluxonRuntime.getInstance()) {
            registerFunction("aiy:block", "block", 0) { FnBlock }
            exportRegistry.registerClass(FnBlock::class.java, "aiy:block")
        }
    }

    @Export
    fun getDrops(block: Block?, @Optional item: ItemStack?, @Optional entity: Entity?): Collection<ItemStack> {
        return block?.getDrops(item, entity) ?: emptyList()
    }
}