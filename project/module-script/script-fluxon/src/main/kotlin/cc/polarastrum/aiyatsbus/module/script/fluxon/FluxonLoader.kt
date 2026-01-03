package cc.polarastrum.aiyatsbus.module.script.fluxon

import org.tabooproject.fluxon.FluxonPlugin
import taboolib.common.LifeCycle
import taboolib.common.Requires
import taboolib.common.platform.Awake

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.script.fluxon.FluxonLoader
 *
 * @author mical
 * @since 2026/1/3 17:40
 */
@Requires(missingClasses = ["!org.tabooproject.fluxon.ParseScript"])
object FluxonLoader {

    @Awake(LifeCycle.INIT)
    fun init() {
        FluxonPlugin.init()
    }
}