package cc.polarastrum.aiyatsbus.module.ingame.command

import cc.polarastrum.aiyatsbus.module.ingame.ui.MainMenuUI
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.mainCommand

@CommandHeader("enchants", permissionDefault = PermissionDefault.TRUE)
object AiyatsbusMenuCommand {
    @CommandBody(permissionDefault = PermissionDefault.TRUE)
    val sub = mainCommand {
        execute<Player> { sender, _, _ -> MainMenuUI.open(sender) }
    }
}