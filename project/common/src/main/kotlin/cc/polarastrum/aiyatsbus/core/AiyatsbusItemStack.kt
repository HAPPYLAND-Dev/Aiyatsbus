package cc.polarastrum.aiyatsbus.core

interface AiyatsbusItemStack {

    fun getEnchants(): Map<AiyatsbusEnchantment, Int>

    fun getLore(): List<String>?
}