package cc.polarastrum.aiyatsbus.module.script.fluxon.function.game

import cc.polarastrum.aiyatsbus.core.util.isDay
import cc.polarastrum.aiyatsbus.core.util.isNight
import cc.polarastrum.aiyatsbus.module.script.fluxon.FluxonScriptHandler
import cc.polarastrum.aiyatsbus.module.script.fluxon.relocate.FluxonRelocate
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.java.Export
import org.tabooproject.fluxon.runtime.java.Optional
import taboolib.common.LifeCycle
import taboolib.common.Requires
import taboolib.common.platform.Awake
import kotlin.math.cos
import kotlin.math.sin

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.script.fluxon.function.game.FnEntity
 *
 * @author mical
 * @since 2026/1/2 00:09
 */
@Requires(missingClasses = ["!org.tabooproject.fluxon.ParseScript"])
@FluxonRelocate
object FnWorld {

    @Awake(LifeCycle.LOAD)
    fun init() {
        FluxonScriptHandler.DEFAULT_PACKAGE_AUTO_IMPORT += "aiy:world"
        with(FluxonRuntime.getInstance()) {
            registerFunction("aiy:world", "world", 0) { FnWorld }
            exportRegistry.registerClass(FnWorld::class.java, "aiy:world")
        }
    }

    @Export
    fun isDay(world: World): Boolean = world.isDay

    @Export
    fun isNight(world: World): Boolean = world.isNight

    @Export
    fun <T> spawnCircleParticles(particle: Particle, loc: Location, amount: Int, option: T, range: Double, @Optional factor: Int?) {
        val factor = factor ?: 10
        for (i in 0 until factor) {
            val angle = 360.0 * i / factor
            val rad = Math.toRadians(angle)
            val sin = range * sin(rad)
            val cos = range * cos(rad)
            loc.world?.spawnParticle(particle, loc.clone().add(sin, 0.0, cos), amount, option)
        }
    }

    @Export
    fun <T> spawnRNAParticles(particle: Particle, loc: Location, amount: Int, option: T, height: Double, range: Double, @Optional factor: Int?, @Optional circle: Int?) {
        val factor = factor ?: 10
        val circle = circle ?: 1

        val totalSteps = factor * circle
        val angleStep = 360.0 / factor
        val heightStep = 2.0 * height / totalSteps

        for (step in 0..totalSteps) {
            val currentAngle = angleStep * step
            val currentHeight = -height + (heightStep * step)

            val rad = Math.toRadians(currentAngle)
            val sin = sin(rad) * range
            val cos = cos(rad) * range

            loc.world?.spawnParticle(particle, loc.clone().add(sin, currentHeight, cos), amount, option)
        }
    }
}