package cc.polarastrum.aiyatsbus.module.ingame.mechanics

import cc.polarastrum.aiyatsbus.core.toDisplayMode
import cc.polarastrum.aiyatsbus.core.toRevertMode
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.PacketEventsAPI
import com.github.retrooper.packetevents.event.EventManager
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.item.HashedStack
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction
import com.github.retrooper.packetevents.wrapper.play.server.*
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.ingame.mechanics.PacketDisplay
 *
 * @author mical
 * @since 2025/8/22 18:33
 */
object PacketDisplay {

    val packetEvents: PacketEventsAPI<*> by lazy { PacketEvents.getAPI() }
    val packetEventManager: EventManager by lazy { packetEvents.eventManager }

    @Awake(LifeCycle.ACTIVE)
    fun registerListener() {
        packetEventManager.registerListener(PacketDisplayListener())
    }

    class PacketDisplayListener : PacketListenerAbstract() {
        override fun onPacketReceive(e: PacketReceiveEvent) {
            val player = e.getPlayer<Player>()
            when(e.packetType) {
                PacketType.Play.Client.CREATIVE_INVENTORY_ACTION -> {
                    val packet = WrapperPlayClientCreativeInventoryAction(e)
                    packet.itemStack = recoverItem(packet.itemStack, player)
                }

                PacketType.Play.Client.CLICK_WINDOW -> {
                    val packet = WrapperPlayClientClickWindow(e)
                    packet.carriedHashedStack = HashedStack.fromItemStack(
                        SpigotConversionUtil.fromBukkitItemStack(player.itemOnCursor.toRevertMode(player))
                    )
                }
            }
        }

        override fun onPacketSend(e: PacketSendEvent) {
            val player = e.getPlayer<Player>()
            when(e.packetType) {
                PacketType.Play.Server.MERCHANT_OFFERS -> {
                    val packet = WrapperPlayServerMerchantOffers(e)
                    packet.merchantOffers.forEach {
                        it.firstInputItem = renderItem(it.firstInputItem, player)
                        it.secondInputItem = renderItem(it.secondInputItem, player)
                        it.outputItem = renderItem(it.outputItem, player)
                    }
                }

                PacketType.Play.Server.SET_PLAYER_INVENTORY -> {
                    val packet = WrapperPlayServerSetPlayerInventory(e)
                    packet.stack = renderItem(packet.stack, player)
                }

                PacketType.Play.Server.SET_SLOT -> {
                    val packet = WrapperPlayServerSetSlot(e)
                    packet.item = renderItem(packet.item, player)
                }

                PacketType.Play.Server.WINDOW_ITEMS -> {
                    val packet = WrapperPlayServerWindowItems(e)
                    packet.items = packet.items.map { renderItem(it, player) }
                    packet.carriedItem.ifPresent {
                        packet.setCarriedItem(renderItem(it, player))
                    }
                }

                PacketType.Play.Server.SET_CURSOR_ITEM -> {
                    val packet = WrapperPlayServerSetCursorItem(e)
                    packet.stack = renderItem(packet.stack, player)
                }

                PacketType.Play.Server.ENTITY_METADATA -> {
                    val packet = WrapperPlayServerEntityMetadata(e)
                    @Suppress("UNCHECKED_CAST")
                    packet.entityMetadata.forEach { data ->
                        if (data.type == EntityDataTypes.ITEMSTACK) {
                            data as EntityData<ItemStack>
                            val item = data.value as ItemStack
                            if (item.type == ItemTypes.ENCHANTED_BOOK) {
                                data.value = renderItem(item, player)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun renderItem(item: ItemStack?, player: Player): ItemStack? {
        if (item == null) return null
        return SpigotConversionUtil.fromBukkitItemStack(
            SpigotConversionUtil.toBukkitItemStack(item).toDisplayMode(player)
        )
    }

    private fun recoverItem(item: ItemStack?, player: Player): ItemStack? {
        if (item == null) return null
        return SpigotConversionUtil.fromBukkitItemStack(
            SpigotConversionUtil.toBukkitItemStack(item).toRevertMode(player)
        )
    }
}