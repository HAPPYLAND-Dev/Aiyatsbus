package com.mcstarrysky.aiyatsbus.module.ingame.command

import com.mcstarrysky.aiyatsbus.module.ingame.command.subcommand.menuSubCommand
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader

@CommandHeader("enchants")
object AiyatsbusMenuCommand {

    @CommandBody
    val sub = menuSubCommand
}