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
package cc.polarastrum.aiyatsbus.module.compat.chat

import cc.polarastrum.aiyatsbus.core.Aiyatsbus.packetEventManager
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.compat.vanilla.PacketSystemChat
 *
 * @author mical
 * @since 2024/2/18 00:40
 */
object PacketSystemChat {

    @Awake(LifeCycle.ACTIVE)
    fun registerListener() {
        packetEventManager.registerListener(PacketChatListener())
    }

    class PacketChatListener : PacketListenerAbstract() {
        override fun onPacketSend(e: PacketSendEvent) {
            val player = e.getPlayer<Player>()
            when(e.packetType) {
                PacketType.Play.Server.CHAT_MESSAGE -> {
                    val wrapper = WrapperPlayServerChatMessage(e)
                    val message = wrapper.message
                    val content = message.chatContent
                    message.chatContent = DisplayReplacer.inst.apply(content, player)
                    wrapper.message = message
                }
                PacketType.Play.Server.SYSTEM_CHAT_MESSAGE -> {
                    val wrapper = WrapperPlayServerSystemChatMessage(e)
                    val message = wrapper.message
                    wrapper.message = DisplayReplacer.inst.apply(message, player)
                }
            }
        }
    }
}