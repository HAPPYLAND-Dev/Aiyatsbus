/*
 *  Copyright (C) 2022-2024 PolarAstrumLab
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package cc.polarastrum.aiyatsbus.module.script.fluxon

import cc.polarastrum.aiyatsbus.core.script.ScriptHandler
import cc.polarastrum.aiyatsbus.core.util.coerceInt
import cc.polarastrum.aiyatsbus.module.script.fluxon.function.FnCommand
import cc.polarastrum.aiyatsbus.module.script.fluxon.function.FnFunctions
import cc.polarastrum.aiyatsbus.module.script.fluxon.function.FnPlayer
import cc.polarastrum.aiyatsbus.module.script.fluxon.function.FnVariables
import cc.polarastrum.aiyatsbus.module.script.fluxon.function.game.FnGuard
import cc.polarastrum.aiyatsbus.module.script.fluxon.function.game.FnBlock
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.tabooproject.fluxon.Fluxon
import org.tabooproject.fluxon.compiler.FluxonFeatures
import org.tabooproject.fluxon.interpreter.bytecode.FluxonClassLoader
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.FluxonRuntimeError
import org.tabooproject.fluxon.runtime.RuntimeScriptBase
import taboolib.common.platform.function.warning
import taboolib.platform.BukkitPlugin
import java.text.ParseException
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.script.fluxon.FluxonScriptHandler
 *
 * @author mical
 * @since 2025/6/22 13:24
 */
class FluxonScriptHandler : ScriptHandler {

    // 服了脚本
    private val compiledScripts = ConcurrentHashMap<UUID, RuntimeScriptBase>()
    private val classLoader = FluxonClassLoader(BukkitPlugin::class.java.classLoader)
    private val environment = FluxonRuntime.getInstance().newEnvironment()

    init {
        FluxonFeatures.DEFAULT_ALLOW_INVALID_REFERENCE = true
        FnGuard.init()
        FnBlock.init()
        FnCommand.init()
        FnFunctions.init()
        FnPlayer.init()
        FnVariables.init()
    }

    override fun invoke(
        source: String,
        sender: CommandSender?,
        variables: Map<String, Any?>
    ): CompletableFuture<Any?>? {
        val uuid = UUID.nameUUIDFromBytes(source.toByteArray())
        if (!compiledScripts.containsKey(uuid)) preheat(source)

        val scriptBase = compiledScripts[uuid] ?: return null

        val environment = FluxonRuntime.getInstance().newEnvironment()
        variables.forEach { (key, value) -> environment.defineRootVariable(key, value) }
        environment.defineRootVariable("sender", sender)
        if (sender is Player) {
            environment.defineRootVariable("player", sender)
        }

        return CompletableFuture.supplyAsync {
            try {
                scriptBase.eval(environment)?.exceptFluxonCompletableFutureError()
            } catch (ex: FluxonRuntimeError) {
                ex.printError()
                null
            }
        }
    }

    override fun preheat(source: String) {
        // 生成一个唯一的标识
        // 脚本无变动, 则无需重复预热
        val uuid = UUID.nameUUIDFromBytes(source.toByteArray())
        if (compiledScripts.containsKey(uuid)) return

        var className = uuid.toString().replace("-", "")
        // 生成唯一的类名
        // 如果开头是数字的话, 就要加一个字母以防无效类名
        // 我喜欢 T
        if (className[0].coerceInt(-1) >= 0) {
            className = "T$className"
        }

        try {
            val result = Fluxon.compile(source, className, environment, BukkitPlugin::class.java.classLoader)
            compiledScripts[uuid] = result.createInstance(classLoader) as RuntimeScriptBase
        } catch (ex: ParseException) {
            warning("编译脚本 $source 时发生错误:")
            ex.printStackTrace()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }
}