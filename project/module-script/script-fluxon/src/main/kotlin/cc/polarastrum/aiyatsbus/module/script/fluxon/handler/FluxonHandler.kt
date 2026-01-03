package cc.polarastrum.aiyatsbus.module.script.fluxon.handler

import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.script.fluxon.handler.FluxonHandler
 *
 * @author mical
 * @since 2026/1/3 16:19
 */
interface FluxonHandler {

    fun invoke(
        source: String,
        sender: CommandSender?,
        variables: Map<String, Any?>
    ): CompletableFuture<Any?>?

    fun preheat(source: String)
}