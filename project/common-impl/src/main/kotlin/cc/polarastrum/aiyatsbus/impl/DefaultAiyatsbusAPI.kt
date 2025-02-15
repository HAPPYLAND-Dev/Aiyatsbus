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
package cc.polarastrum.aiyatsbus.impl

import cc.polarastrum.aiyatsbus.core.*
import cc.polarastrum.aiyatsbus.core.registration.AiyatsbusEnchantmentRegisterer
import cc.polarastrum.aiyatsbus.core.registration.modern.ModernEnchantmentRegisterer
import cc.polarastrum.aiyatsbus.impl.registration.legacy.DefaultLegacyEnchantmentRegisterer
import taboolib.common.UnsupportedVersionException
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.function.info
import taboolib.common.util.t
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy
import java.util.concurrent.CompletableFuture

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.impl.DefaultAiyatsbusAPI
 *
 * @author mical
 * @since 2024/2/17 16:20
 */
class DefaultAiyatsbusAPI : AiyatsbusAPI {

    private val enchantmentFilter = PlatformFactory.getAPI<AiyatsbusEnchantmentFilter>()

    private val enchantmentManager = PlatformFactory.getAPI<AiyatsbusEnchantmentManager>()

    private val displayManager = PlatformFactory.getAPI<AiyatsbusDisplayManager>()

    private val language = PlatformFactory.getAPI<AiyatsbusLanguage>()

    private val playerDataHandler = PlatformFactory.getAPI<AiyatsbusPlayerDataHandler>()

    private val enchantmentRegisterer0: AiyatsbusEnchantmentRegisterer by lazy {
        when {
            MinecraftVersion.versionId >= 12104 ->
                proxy<ModernEnchantmentRegisterer>("cc.polarastrum.aiyatsbus.impl.registration.v12104_nms.DefaultModernEnchantmentRegisterer")
            MinecraftVersion.versionId >= 12102 ->
                proxy<ModernEnchantmentRegisterer>("cc.polarastrum.aiyatsbus.impl.registration.v12103_nms.DefaultModernEnchantmentRegisterer")
            MinecraftVersion.versionId >= 12100 ->
                proxy<ModernEnchantmentRegisterer>("cc.polarastrum.aiyatsbus.impl.registration.v12100_nms.DefaultModernEnchantmentRegisterer")
            MinecraftVersion.versionId >= 12005 -> throw UnsupportedVersionException()
            MinecraftVersion.versionId >= 12003 ->
                proxy<ModernEnchantmentRegisterer>("cc.polarastrum.aiyatsbus.impl.registration.v12004_nms.DefaultModernEnchantmentRegisterer")
            else -> DefaultLegacyEnchantmentRegisterer
        }
    }

    private val eventExecutor = PlatformFactory.getAPI<AiyatsbusEventExecutor>()

    private val ketherHandler = PlatformFactory.getAPI<AiyatsbusKetherHandler>()

    private val minecraftAPI0 by lazy {
        proxy<AiyatsbusMinecraftAPI>("cc.polarastrum.aiyatsbus.impl.nms.DefaultAiyatsbusMinecraftAPI")
    }

    private val tickHandler = PlatformFactory.getAPI<AiyatsbusTickHandler>()

    override fun getEnchantmentFilter(): AiyatsbusEnchantmentFilter {
        return enchantmentFilter
    }

    override fun getEnchantmentManager(): AiyatsbusEnchantmentManager {
        return enchantmentManager
    }

    override fun getEnchantmentRegisterer(): AiyatsbusEnchantmentRegisterer {
        return enchantmentRegisterer0
    }

    override fun getEventExecutor(): AiyatsbusEventExecutor {
        return eventExecutor
    }

    override fun getKetherHandler(): AiyatsbusKetherHandler {
        return ketherHandler
    }

    override fun getDisplayManager(): AiyatsbusDisplayManager {
        return displayManager
    }

    override fun getLanguage(): AiyatsbusLanguage {
        return language
    }

    override fun getMinecraftAPI(): AiyatsbusMinecraftAPI {
        return minecraftAPI0
    }

    override fun getPlayerDataHandler(): AiyatsbusPlayerDataHandler {
        return playerDataHandler
    }

    override fun getTickHandler(): AiyatsbusTickHandler {
        return tickHandler
    }

    private inline fun <reified T> proxy(bind: String, vararg parameter: Any): T {
        val time = System.currentTimeMillis()
        val proxy = nmsProxy(T::class.java, bind, *parameter)
        val cost = System.currentTimeMillis() - time
        info("""
            代理类 ${T::class.java.simpleName} 已生成，用时 $cost 毫秒。
            Generated ${T::class.java.simpleName} in ${System.currentTimeMillis() - time}ms
        """.t())
        return proxy
    }

    init {
        CompletableFuture.runAsync {
            enchantmentRegisterer0
            minecraftAPI0
        }
    }
}