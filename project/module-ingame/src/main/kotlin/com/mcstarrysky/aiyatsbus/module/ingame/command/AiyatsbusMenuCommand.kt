package com.mcstarrysky.aiyatsbus.module.ingame.command

import com.mcstarrysky.aiyatsbus.module.ingame.ui.MainMenuUI
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand

@CommandHeader("enchants")
object AiyatsbusMenuCommand {

    @CommandBody
    val sub = mainCommand {
        execute<Player> { sender, _, _ -> MainMenuUI.open(sender) }
    }
}