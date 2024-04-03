package com.mcstarrysky.aiyatsbus.module.ingame.command.subcommand

import com.mcstarrysky.aiyatsbus.core.AiyatsbusEnchantment
import com.mcstarrysky.aiyatsbus.core.aiyatsbusEt
import com.mcstarrysky.aiyatsbus.core.book
import com.mcstarrysky.aiyatsbus.core.sendLang
import com.mcstarrysky.aiyatsbus.module.ingame.command.AiyatsbusCommand
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestPlayers
import taboolib.platform.util.giveItem

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.module.command.subcommand.Book
 *
 * @author mical
 * @since 2024/3/3 17:40
 */
val bookSubCommand = subCommand cmd@{
    dynamic("enchant") {
        suggestion<CommandSender> { _, _ -> AiyatsbusCommand.enchantNamesAndIds }
        execute<CommandSender> { sender, args, _ -> handleBook(sender, null, aiyatsbusEt(args["enchant"])!!) }
        dynamic("level", true) {
            suggestion<Player> level@{ _, cmd ->
                val maxLevel = (aiyatsbusEt(cmd["enchant"])?.basicData?.maxLevel ?: return@level listOf())
                buildList { repeat(maxLevel) { add("${it + 1}") } }
            }
            execute<CommandSender> { sender, args, _ -> handleBook(sender, null, aiyatsbusEt(args["enchant"])!!, args["level"].toInt()) }
            dynamic("player", true) {
                suggestPlayers()
                execute<CommandSender> { sender, args, _ -> handleBook(sender, args["player"], aiyatsbusEt(args["enchant"])!!, args["level"].toInt()) }
            }
        }
    }
}

private fun handleBook(sender: CommandSender, who: String?, enchant: AiyatsbusEnchantment, level: Int = enchant.basicData.maxLevel) {
    (who?.let { Bukkit.getPlayer(it) } ?: (sender as? Player))?.let { receiver ->
        receiver.giveItem(enchant.book(level))
        sender.sendLang("command-subCommands-book-sender", receiver.name to "name")
        receiver.sendLang("command-subCommands-book-receiver", enchant.displayName(level) to "enchantment")
    } ?: sender.sendLang("command-subCommands-book-fail")
}