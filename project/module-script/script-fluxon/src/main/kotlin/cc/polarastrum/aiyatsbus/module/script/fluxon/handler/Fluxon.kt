package cc.polarastrum.aiyatsbus.module.script.fluxon.handler

import cc.polarastrum.aiyatsbus.core.util.coerceInt
import cc.polarastrum.aiyatsbus.module.script.fluxon.FluxonScriptHandler
import cc.polarastrum.aiyatsbus.module.script.fluxon.relocate.FluxonRelocate
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.tabooproject.fluxon.Fluxon
import org.tabooproject.fluxon.compiler.CompilationContext
import org.tabooproject.fluxon.interpreter.bytecode.FluxonClassLoader
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.RuntimeScriptBase
import org.tabooproject.fluxon.runtime.error.FluxonRuntimeError
import org.tabooproject.fluxon.util.exceptFluxonCompletableFutureError
import org.tabooproject.fluxon.util.printError
import taboolib.common.Requires
import taboolib.common.platform.function.warning
import taboolib.platform.BukkitPlugin
import java.text.ParseException
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * 本类无实际意义，供 ASM 生成
 *
 * @author mical
 * @since 2026/1/3 13:55
 */
@Requires(missingClasses = ["!org.tabooproject.fluxon.ParseScript"])
@FluxonRelocate
object Fluxon : FluxonHandler {

    // 服了脚本
    private val compiledScripts = ConcurrentHashMap<UUID, RuntimeScriptBase>()
    private val classLoader = FluxonClassLoader(BukkitPlugin::class.java.classLoader)
    private val environment = FluxonRuntime.getInstance().newEnvironment()

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
            } catch (ex: Throwable) {
                ex.printStackTrace()
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
            val result = Fluxon.compile(
                environment,
                CompilationContext(source).apply { packageAutoImport += FluxonScriptHandler.DEFAULT_PACKAGE_AUTO_IMPORT },
                className,
                classLoader
            )
            compiledScripts[uuid] = result.createInstance(classLoader) as RuntimeScriptBase
        } catch (ex: ParseException) {
            warning("编译脚本 $source 时发生错误:")
            ex.printStackTrace()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }
}