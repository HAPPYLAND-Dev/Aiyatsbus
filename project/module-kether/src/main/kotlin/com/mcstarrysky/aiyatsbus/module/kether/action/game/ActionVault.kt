package com.mcstarrysky.aiyatsbus.module.kether.action.game

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import taboolib.platform.compat.depositBalance
import taboolib.platform.compat.getBalance
import taboolib.platform.compat.isEconomySupported
import taboolib.platform.compat.withdrawBalance

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.module.kether.action.game.ActionVault
 *
 * @author mical
 * @since 2025/1/12 11:41
 */
object ActionVault {

    // TODO: 空指针检查

    @KetherParser(["money"], shared = true, namespace = "aiyatsbus")
    fun moneyParser() = combinationParser {
        it.group(any()).apply(it) { player -> now { getBalance(player(player) ?: return@now 0.0) } }
    }

    @KetherParser(["take-money"], shared = true, namespace = "aiyatsbus")
    fun takeMoneyParser() = combinationParser {
        it.group(any(), double()).apply(it) { player, balance -> now { withdrawBalance(player(player) ?: return@now, balance) } }
    }

    @KetherParser(["give-money"], shared = true, namespace = "aiyatsbus")
    fun giveMoneyParser() = combinationParser {
        it.group(any(), double()).apply(it) { player, balance -> now { depositBalance(player(player) ?: return@now, balance) } }
    }

    @KetherParser(["has-money"], shared = true, namespace = "aiyatsbus")
    fun hasMoneyParser() = combinationParser {
        it.group(any(), double()).apply(it) { player, balance -> now { hasBalance(player(player) ?: return@now false, balance) } }
    }

    private fun player(any: Any?): Player? {
        return if (any is Player) any else if (any is String) Bukkit.getPlayerExact(any) else null
    }

    private fun hasBalance(player: Player, balance: Double): Boolean {
        return if (!isEconomySupported) false else player.getBalance() >= balance
    }

    private fun getBalance(player: Player): Double {
        return if (isEconomySupported) player.getBalance() else 0.0
    }

    private fun depositBalance(player: Player, balance: Double) {
        if (isEconomySupported) {
            player.depositBalance(balance)
        }
    }

    private fun withdrawBalance(player: Player, balance: Double) {
        if (isEconomySupported) {
            player.withdrawBalance(balance)
        }
    }
}