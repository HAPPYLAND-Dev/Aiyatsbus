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
package cc.polarastrum.aiyatsbus.module.ingame.command.subcommand

import cc.polarastrum.aiyatsbus.core.*
import cc.polarastrum.aiyatsbus.core.compat.EnchantRegistrationHooks
import cc.polarastrum.aiyatsbus.core.data.registry.RarityLoader
import cc.polarastrum.aiyatsbus.core.registration.modern.ModernEnchantmentRegisterer
import cc.polarastrum.aiyatsbus.core.util.inject.Reloadables
import cc.polarastrum.aiyatsbus.module.ingame.command.AiyatsbusCommand
import cc.polarastrum.aiyatsbus.module.ingame.mechanics.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.util.ResettableLazy
import taboolib.module.lang.Language
import taboolib.platform.util.onlinePlayers

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.module.command.subcommand.Reload
 *
 * @author mical
 * @since 2024/3/3 19:33
 */
val reloadSubCommand = subCommand {
    execute<CommandSender> { sender, _, _ ->
        val time = System.currentTimeMillis()
        (Aiyatsbus.api().getEnchantmentRegisterer() as? ModernEnchantmentRegisterer)?.replaceRegistry()
        Language.reload()
        AiyatsbusSettings.conf.reload()
        Reloadables.execute()
        Aiyatsbus.api().getDisplayManager().getSettings().conf.reload()
        AnvilSupport.conf.reload()
        EnchantingTableSupport.conf.reload()
        ExpModifier.conf.reload()
        GrindstoneSupport.conf.reload()
        VillagerSupport.conf.reload()
        onlinePlayers.forEach(Player::updateInventory)
        AiyatsbusCommand.init() // 重新生成 TabList
        ResettableLazy.reset()
        sender.sendLang("plugin-reload", System.currentTimeMillis() - time)
        EnchantRegistrationHooks.unregisterHooks()
        EnchantRegistrationHooks.registerHooks()
    }
}