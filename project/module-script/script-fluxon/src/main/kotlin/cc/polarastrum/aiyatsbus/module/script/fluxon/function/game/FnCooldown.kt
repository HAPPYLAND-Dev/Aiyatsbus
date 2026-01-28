package cc.polarastrum.aiyatsbus.module.script.fluxon.function.game

import cc.polarastrum.aiyatsbus.core.AiyatsbusEnchantment
import cc.polarastrum.aiyatsbus.core.util.addCd
import cc.polarastrum.aiyatsbus.core.util.checkCd
import cc.polarastrum.aiyatsbus.core.util.clearCd
import cc.polarastrum.aiyatsbus.core.util.coerceBoolean
import cc.polarastrum.aiyatsbus.core.util.removeCd
import cc.polarastrum.aiyatsbus.core.util.sendCooldownTip
import cc.polarastrum.aiyatsbus.module.script.fluxon.FluxonScriptHandler
import cc.polarastrum.aiyatsbus.module.script.fluxon.relocate.FluxonRelocate
import org.bukkit.entity.Player
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.java.Export
import org.tabooproject.fluxon.runtime.java.Optional
import taboolib.common.LifeCycle
import taboolib.common.Requires
import taboolib.common.platform.Awake

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.script.fluxon.function.game.FnEntity
 *
 * @author mical
 * @since 2026/1/2 00:09
 */
@Requires(missingClasses = ["!org.tabooproject.fluxon.ParseScript"])
@FluxonRelocate
object FnCooldown {

    @Awake(LifeCycle.LOAD)
    fun init() {
        FluxonScriptHandler.DEFAULT_PACKAGE_AUTO_IMPORT += "aiy:cooldown"
        with(FluxonRuntime.getInstance()) {
            registerFunction("aiy:cooldown", "cooldown", 0) { FnCooldown }
            exportRegistry.registerClass(FnCooldown::class.java, "aiy:cooldown")
        }
    }

    @Export
//    fun isReady(player: Player, key: String, seconds: Double, @Optional broadcast: Boolean?, @Optional broadcastInActionBar: Boolean?): Boolean {
    fun isReady(player: Player, enchant: AiyatsbusEnchantment, seconds: Double, @Optional broadcast: Boolean?, @Optional broadcastInActionBar: Boolean?): Boolean {
        val (isReady, remainingTime) = player.checkCd(enchant.basicData.id, seconds)
        if (!isReady && broadcast.coerceBoolean(true)) {
            player.sendCooldownTip(remainingTime, broadcastInActionBar)
        }
        return isReady
    }

//    @Export
//    fun isReady(player: Player, enchant: AiyatsbusEnchantment, seconds: Double, @Optional broadcast: Boolean?, @Optional broadcastInActionBar: Boolean?): Boolean {
//        return isReady(player, enchant.basicData.id, seconds, broadcast, broadcastInActionBar)
//    }

    @Export
//    fun addCooldown(player: Player, key: String) {
    fun addCooldown(player: Player, enchant: AiyatsbusEnchantment) {
        player.addCd(enchant.basicData.id)
    }

//    @Export
//    fun addCooldown(player: Player, enchant: AiyatsbusEnchantment) {
//        addCooldown(player, enchant.basicData.id)
//    }

    @Export
//    fun removeCooldown(player: Player, key: String) {
    fun removeCooldown(player: Player, enchant: AiyatsbusEnchantment) {
        player.removeCd(enchant.basicData.id)
    }

//    @Export
//    fun removeCooldown(player: Player, enchant: AiyatsbusEnchantment) {
//        removeCooldown(player, enchant.basicData.id)
//    }

    @Export
    fun clearCooldown(player: Player) {
        player.clearCd()
    }
}