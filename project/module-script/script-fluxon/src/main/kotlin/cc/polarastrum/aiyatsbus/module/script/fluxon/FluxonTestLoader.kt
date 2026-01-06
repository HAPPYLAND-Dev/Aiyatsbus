package cc.polarastrum.aiyatsbus.module.script.fluxon

import org.tabooproject.fluxon.FluxonCommand
import taboolib.common.LifeCycle
import taboolib.common.Requires
import taboolib.common.inject.ClassVisitorHandler
import taboolib.common.platform.Awake
import taboolib.library.reflex.ReflexClass

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.script.fluxon.FluxonTestLoader
 *
 * @author mical
 * @since 2026/1/5 23:56
 */
@Requires(missingClasses = ["!org.tabooproject.fluxon.runtime.FluxonRuntime"])
object FluxonTestLoader {

    @Awake(LifeCycle.CONST)
    fun init() {
        ClassVisitorHandler.injectAll(ReflexClass.of(FluxonCommand::class.java))
    }
}