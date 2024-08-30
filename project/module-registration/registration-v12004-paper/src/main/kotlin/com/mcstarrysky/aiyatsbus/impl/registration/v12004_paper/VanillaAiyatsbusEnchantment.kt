package com.mcstarrysky.aiyatsbus.impl.registration.v12004_paper

import com.mcstarrysky.aiyatsbus.core.Aiyatsbus
import com.mcstarrysky.aiyatsbus.core.AiyatsbusEnchantment
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.world.inventory.ContainerAnvil
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentSlotType
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import java.util.*

/**
 * TabooLib nmsProxy 有些问题, 又用不了 paperweight (也没必要用)
 * 只能用这么丑陋的办法来实现, 还好就这一个版本...
 *
 * @author mical
 * @date 2024/8/21 01:32
 */
class VanillaAiyatsbusEnchantment(val id: String) : net.minecraft.world.item.enchantment.Enchantment(
    Rarity.d, // VERT_RARE
    EnchantmentSlotType.n, // VANISHABLE
    emptyArray()
) {

    private val enchantmentKey: NamespacedKey = NamespacedKey.minecraft(id)

    private val enchant: AiyatsbusEnchantment?
        get() = Aiyatsbus.api().getEnchantmentManager().getEnchant(enchantmentKey)

    // canEnchantItem
    override fun a(stack: ItemStack): Boolean {
        // 使 Aiyatsbus 接管铁砧
        val caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).callerClass
        if (caller.name == ContainerAnvil::class.java.name) {
            return false
        }

        val item = CraftItemStack.asCraftMirror(stack)
        return enchant?.canEnchantItem(item) ?: false
    }

    // getMinLevel
    override fun e(): Int = 1

    // getMaxLevel
    override fun a(): Int = enchant?.basicData?.maxLevel ?: 1

    // isCurse
    override fun c(): Boolean = false

    // isDiscoverable
    override fun i(): Boolean = false

    // isTradeable
    override fun h(): Boolean = false

    // isTreasureOnly
    override fun b(): Boolean = true

    // getMinCost
//    override fun a(var0: Int): Int {
//        return Int.MAX_VALUE
//    }
//
    // getMaxCost
//    override fun b(var0: Int): Int {
//        return Int.MAX_VALUE
//    }

    // getFullname
    override fun d(level: Int): IChatBaseComponent {
        return if (enchant != null) {
            IChatBaseComponent::class.java.invokeMethod<IChatBaseComponent>("a", enchant!!.displayName(level), remap = false)!!
        } else super.d(level) // super.getFullname(level)
    }

    override fun toString(): String {
        return "VanillaAiyatsbusEnchantment(id='$id')"
    }

    override fun equals(other: Any?): Boolean {
        return other is VanillaAiyatsbusEnchantment && other.id == this.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}