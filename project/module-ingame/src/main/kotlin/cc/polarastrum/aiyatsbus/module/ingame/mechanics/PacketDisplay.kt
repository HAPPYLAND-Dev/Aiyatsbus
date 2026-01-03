package cc.polarastrum.aiyatsbus.module.ingame.mechanics

import cc.polarastrum.aiyatsbus.core.Aiyatsbus
import cc.polarastrum.aiyatsbus.core.Aiyatsbus.packetEventManager
import cc.polarastrum.aiyatsbus.core.toDisplayMode
import cc.polarastrum.aiyatsbus.core.toRevertMode
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound
import com.github.retrooper.packetevents.protocol.nbt.NBTFloat
import com.github.retrooper.packetevents.protocol.nbt.NBTList
import com.github.retrooper.packetevents.protocol.nbt.NBTType
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.util.NbtCodec
import com.github.retrooper.packetevents.protocol.util.NbtCodecException
import com.github.retrooper.packetevents.protocol.util.NbtCodecs
import com.github.retrooper.packetevents.protocol.util.NbtEncoder
import com.github.retrooper.packetevents.protocol.util.NbtMapCodec
import com.github.retrooper.packetevents.protocol.util.NbtMapEncoder
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityTypes
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.client.*
import com.github.retrooper.packetevents.wrapper.play.server.*
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.ingame.mechanics.PacketDisplay
 *
 * @author mical
 * @since 2025/8/22 18:33
 */
object PacketDisplay {

    @Awake(LifeCycle.ACTIVE)
    fun registerListener() {
        packetEventManager.registerListener(PacketDisplayListener())
    }

    @SubscribeEvent
    fun e(e: taboolib.module.nms.PacketReceiveEvent) {
        if (e.packet.name == "PacketPlayInWindowClick" || e.packet.name == "ServerboundContainerClickPacket") {
            Aiyatsbus.api().getMinecraftAPI().getPacketHandler().handleContainerClick(e)
        }
    }

    class PacketDisplayListener : PacketListenerAbstract() {
        override fun onPacketReceive(e: PacketReceiveEvent) {
            val player = e.getPlayer<Player>()
            when(e.packetType) {
                PacketType.Play.Client.CREATIVE_INVENTORY_ACTION -> {
                    val packet = WrapperPlayClientCreativeInventoryAction(e)
                    packet.itemStack = recoverItem(packet.itemStack, player)
                }

//                PacketType.Play.Client.CLICK_WINDOW -> {
//                    val packet = WrapperPlayClientClickWindow(e)
//                    if (packet.windowClickType != WrapperPlayClientClickWindow.WindowClickType.QUICK_CRAFT) return
//
//                    val cursor = player.openInventory.cursor
//                    if (packet.carriedHashedStack.isPresent) {
//                        val reverted = SpigotConversionUtil.fromBukkitItemStack(cursor.toRevertMode(player))
//                        val hashed = HashedStack.fromItemStack(recoverItem(reverted, player))
//                        packet.carriedHashedStack = hashed
//                    }
//
//                    if (packet.hashedSlots.size == 1 && cursor.amount == 1) {
//                        val itemsToRecover = mutableMapOf<Int, Optional<HashedStack>>()
//                        val reverted = SpigotConversionUtil.fromBukkitItemStack(cursor.toRevertMode(player))
//                        val hashed = HashedStack.fromItemStack(recoverItem(reverted, player)) // 操你妈, 半残实现害我差一天
//                        packet.hashedSlots.keys.forEach { itemsToRecover[it] = hashed }
//                        packet.hashedSlots = itemsToRecover
//                        e.markForReEncode(true)
//                    }
//                }
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

                PacketType.Play.Server.BLOCK_ENTITY_DATA -> {
                    val packet = WrapperPlayServerBlockEntityData(e)
                    if (packet.blockEntityType == BlockEntityTypes.SHELF) {
                        val items = packet.nbt.getCompoundListTagOrNull("Items") ?: return
                        if (items.isEmpty) return
                        repeat(items.size()) { i ->
                            val item = items.getTag(i)
                            if (item.getStringTagValueOrNull("id") != "minecraft:enchanted_book") return@repeat

                            val compound = item.getCompoundTagOrNull("components") ?: return@repeat
                            val enchantment = compound.getCompoundTagOrNull("minecraft:stored_enchantments") ?: return@repeat
                            val model = enchantment.tagNames
                                .mapNotNull { namedId ->
                                    Aiyatsbus.api().getEnchantmentManager().getEnchant(NamespacedKey.fromString(namedId)!!)
                                }
                                .filter { it.rarity.isCustomModelBookEnabled }
                                .minByOrNull { it.rarity.weight }?.rarity?.customModelBook ?: return@repeat

                            compound.set("minecraft:custom_model_data", mapOf("floats" to listOf(model.toFloat())), CODEC, packet)
                        }

                        packet.nbt.setTag("Items", items)
                    }
                }
            }
        }
    }

    val CODEC: NbtCodec<Map<String, List<Float>>> =
        object : NbtMapCodec<Map<String, List<Float>>> {

            @Throws(NbtCodecException::class)
            override fun decode(
                compound: NBTCompound,
                wrapper: PacketWrapper<*>
            ): Map<String, List<Float>> {

                val result = HashMap<String, List<Float>>()

                for (key in compound.tagNames) {
                    val listTag = compound.getListOrEmpty(
                        key,
                        NbtCodecs.FLOAT,
                        wrapper
                    )

                    val list = ArrayList<Float>(listTag.size)
                    for (nbtFloat in listTag) {
                        list.add(nbtFloat)
                    }

                    result[key] = list
                }

                return result
            }

            @Throws(NbtCodecException::class)
            override fun encode(
                compound: NBTCompound,
                wrapper: PacketWrapper<*>,
                value: Map<String, List<Float>>
            ) {
                for ((key, list) in value) {
                    val listTag = NBTList(NBTType.FLOAT)

                    for (f in list) {
                        listTag.addTag(NBTFloat(f))
                    }

                    compound.setTag(key, listTag)
                }
            }
        }.codec()

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