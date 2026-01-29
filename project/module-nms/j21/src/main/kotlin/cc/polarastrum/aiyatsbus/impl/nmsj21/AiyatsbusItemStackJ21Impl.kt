package cc.polarastrum.aiyatsbus.impl.nmsj21

import cc.polarastrum.aiyatsbus.core.AiyatsbusEnchantment
import cc.polarastrum.aiyatsbus.core.AiyatsbusItemStack
import cc.polarastrum.aiyatsbus.core.aiyatsbusEt
import cc.polarastrum.aiyatsbus.core.util.LEGACY_COMPONENT_SERIALIZER
import io.papermc.paper.adventure.PaperAdventure
import net.minecraft.core.component.DataComponents
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.craftbukkit.util.CraftNamespacedKey
import org.bukkit.inventory.ItemStack

class AiyatsbusItemStackJ21Impl(item: ItemStack) : AiyatsbusItemStack {

    val handle: NMSItemStack = if (item is CraftItemStack) item.handle else CraftItemStack.asNMSCopy(item)

    override fun getEnchants(): Map<AiyatsbusEnchantment, Int> {
        val stored = handle.get(DataComponents.STORED_ENCHANTMENTS) ?: handle.get(DataComponents.ENCHANTMENTS)
        if (stored == null) {
            return emptyMap()
        }
        val entries = stored.entrySet()
        if (entries.isEmpty()) {
            return emptyMap()
        }
        val map = HashMap<AiyatsbusEnchantment, Int>(entries.size)
        for (entry in entries) {
            val enchantment = entry.key
            val level = entry.value
            map[aiyatsbusEt(CraftNamespacedKey.fromMinecraft(enchantment.unwrapKey().get().location()))!!] = level
        }
        return map
    }

    override fun getLore(): List<String>? {
        return handle.get(DataComponents.LORE)?.lines?.map { LEGACY_COMPONENT_SERIALIZER.serialize(PaperAdventure.asAdventure(it)) }
    }

    override fun isUnbreakable(): Boolean {
        return handle.get(DataComponents.UNBREAKABLE)?.showInTooltip ?: return false
    }
}