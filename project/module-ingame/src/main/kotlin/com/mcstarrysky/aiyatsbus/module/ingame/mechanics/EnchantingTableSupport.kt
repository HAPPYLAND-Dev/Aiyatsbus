package com.mcstarrysky.aiyatsbus.module.ingame.mechanics

import com.mcstarrysky.aiyatsbus.core.*
import com.mcstarrysky.aiyatsbus.core.data.CheckType
import com.mcstarrysky.aiyatsbus.core.util.calcToDouble
import com.mcstarrysky.aiyatsbus.core.util.calcToInt
import com.mcstarrysky.aiyatsbus.core.util.serialized
import com.mcstarrysky.aiyatsbus.module.ingame.ui.internal.function.round
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.conversion
import taboolib.module.nms.PacketSendEvent
import kotlin.math.roundToInt

@ConfigNode(bind = "core/mechanisms/enchanting_table.yml")
object EnchantingTableSupport {

    private val shelfAmount = mutableMapOf<String, Int>()

    @Config("core/mechanisms/enchanting_table.yml", autoReload = true)
    lateinit var conf: Configuration
        private set

    @ConfigNode("vanilla_table")
    var vanillaTable = false
    @ConfigNode("more_enchant_chance")
    var moreEnchantChance = listOf("0.2*{cost}", "0.15*{cost}", "0.1*{cost}")
    @ConfigNode("level_formula")
    var levelFormula = "{cost}/3*{max_level}+{cost}*({random}-{random})"
    @ConfigNode("privilege.full_level")
    var fullLevelPrivilege = "aiyatsbus.privilege.table.full"

    @delegate:ConfigNode("privilege.chance")
    val moreEnchantPrivilege by conversion<List<String>, Map<String, String>> {
        mapOf(*map { it.split(":")[0] to it.split(":")[1] }.toTypedArray())
    }

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(e: PacketSendEvent) {
        if (e.packet.name == "PacketPlayOutWindowData") {
            runCatching {
                val a = e.packet.read<Int>("b", false)
                if (a in 4..6) {
                    e.packet.write("c", -1)
                }
            }.onFailure { it.printStackTrace() }
        }
    }

    // 记录附魔台的书架等级
    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun prepareEnchant(event: PrepareItemEnchantEvent) {
        if (vanillaTable)
            return
        shelfAmount[event.enchantBlock.location.serialized] = event.enchantmentBonus.coerceAtMost(16)
    }

    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun enchant(event: EnchantItemEvent) {
        if (vanillaTable)
            return

        val player = event.enchanter
        val item = event.item.clone()
        val cost = event.whichButton() + 1
        val bonus = shelfAmount[event.enchantBlock.location.serialized] ?: 1

        val result = enchant(player, item, cost, bonus)

        result.first.ifEmpty {
            event.isCancelled = true
            return
        }

        event.enchantsToAdd.clear()
        event.enchantsToAdd.putAll(result.first.mapKeys { it.key as Enchantment })

        // 对书的附魔，必须手动进行，因为原版处理会掉特殊附魔
        // 也许可以用更好的方法兼容，submit有一定风险 FIXME
        if (item.type == Material.BOOK) {
            submit {
                event.inventory.setItem(0, result.second)
            }
        }
    }

    fun enchant(
        player: Player,
        item: ItemStack,
        cost: Int = 3,
        bonus: Int = 16,
        checkType: CheckType = CheckType.ATTAIN
    ): Pair<Map<AiyatsbusEnchantment, Int>, ItemStack> {
        val enchantsToAdd = mutableMapOf<AiyatsbusEnchantment, Int>()
        val result = item.clone()
        if (item.type == Material.BOOK) result.type = Material.ENCHANTED_BOOK

        val amount = enchantAmount(player, cost)
        val pool = result.etsAvailable(checkType, player).filter { it.alternativeData.isDiscoverable }

        repeat(amount) {
            val enchant = pool.drawEt() ?: return@repeat
            val maxLevel = enchant.basicData.maxLevel
            val level = if (player.hasPermission(fullLevelPrivilege)) maxLevel
            else levelFormula.calcToInt("bonus" to bonus, "max_level" to maxLevel, "cost" to cost, "random" to Math.random().round(3))
                .coerceAtLeast(1)
                .coerceAtMost(maxLevel)

            if (enchant.limitations.checkAvailable(checkType, result, player).first) {
                result.addEt(enchant)
                enchantsToAdd[enchant] = level
            }
        }

        return enchantsToAdd to result
    }

    private fun enchantAmount(player: Player, cost: Int) = moreEnchantChance.count {
        Math.random() <= finalChance(it.calcToDouble("cost" to cost), player)
    }.coerceAtLeast(1)

    private fun finalChance(origin: Double, player: Player) = moreEnchantPrivilege.maxOf { (perm, expression) ->
        if (player.hasPermission(perm)) expression.calcToInt("chance" to origin)
        else origin.roundToInt()
    }.coerceAtLeast(0)
}