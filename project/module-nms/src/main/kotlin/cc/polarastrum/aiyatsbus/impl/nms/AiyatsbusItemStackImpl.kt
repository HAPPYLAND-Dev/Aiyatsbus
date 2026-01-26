package cc.polarastrum.aiyatsbus.impl.nms

import cc.polarastrum.aiyatsbus.core.Aiyatsbus
import cc.polarastrum.aiyatsbus.core.AiyatsbusEnchantment
import cc.polarastrum.aiyatsbus.core.AiyatsbusItemStack
import cc.polarastrum.aiyatsbus.core.aiyatsbusEt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.item.ItemEnchantedBook
import net.minecraft.world.item.Items
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers
import org.bukkit.inventory.ItemStack
import taboolib.module.chat.Components
import kotlin.experimental.and

class AiyatsbusItemStackImpl(item: ItemStack): AiyatsbusItemStack {

    val handle = (if (item is CraftItemStack) Aiyatsbus.api().getMinecraftAPI().getHelper().getCraftItemStackHandle(item) else CraftItemStack.asNMSCopy(item)) as NMSItemStack

    override fun getEnchants(): Map<AiyatsbusEnchantment, Int> {
        val enchantmentNBT = if (handle.item == Items.ENCHANTED_BOOK)
            ItemEnchantedBook.getEnchantments(handle)
        else handle.enchantmentTags

        val map = HashMap<AiyatsbusEnchantment, Int>()

        for (base in enchantmentNBT) {
            val compound = base as NBTTagCompound
            val key = NamespacedKey.fromString(compound.getString("id"))
            val level = ('\uffff'.code.toShort() and compound.getShort("lvl")).toInt()
            if (key != null) {
                val enchant = aiyatsbusEt(key)
                if (enchant != null) {
                    map[enchant] = level
                }
            }
        }
        return map
    }

    override fun getLore(): List<String>? {
        val displayTag = handle.getTagElement("display") ?: return emptyList()

        if (!displayTag.contains("Lore")) {
            return emptyList()
        }

        val loreTag = displayTag.getList("Lore", CraftMagicNumbers.NBT.TAG_STRING)

        return loreTag.indices.map { Components.parseRaw(loreTag.getString(it)).toLegacyText() }
    }
}