package cc.polarastrum.aiyatsbus.module.script.fluxon.function.game

import cc.polarastrum.aiyatsbus.core.AiyatsbusEnchantment
import cc.polarastrum.aiyatsbus.core.AiyatsbusSettings
import cc.polarastrum.aiyatsbus.core.asLang
import cc.polarastrum.aiyatsbus.core.util.addCd
import cc.polarastrum.aiyatsbus.core.util.checkCd
import cc.polarastrum.aiyatsbus.core.util.clearCd
import cc.polarastrum.aiyatsbus.core.util.coerceBoolean
import cc.polarastrum.aiyatsbus.core.util.removeCd
import cc.polarastrum.aiyatsbus.module.script.fluxon.FluxonScriptHandler
import cc.polarastrum.aiyatsbus.module.script.fluxon.relocate.FluxonRelocate
import org.bukkit.entity.Player
import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.java.Export
import org.tabooproject.fluxon.runtime.java.Optional
import taboolib.common.LifeCycle
import taboolib.common.Requires
import taboolib.common.platform.Awake
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.component
import taboolib.module.nms.sendRawActionBar

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
    fun isReady(player: Player, key: String, seconds: Double, @Optional broadcast: Boolean?, @Optional broadcastInActionBar: Boolean?): Boolean {
        val (isReady, remainingTime) = player.checkCd(key, seconds)
        if (!isReady && broadcast.coerceBoolean(true)) {
            val message = player.asLang("messages-misc-cool_down", remainingTime to "second").component().buildColored()
            if (broadcastInActionBar.coerceBoolean(AiyatsbusSettings.coolDownInActionBar)) {
                player.sendRawActionBar(message.toRawMessage())
            } else {
                message.sendTo(adaptPlayer(player))
            }
        }
        return isReady
    }

    @Export
    fun isReady(player: Player, enchant: AiyatsbusEnchantment, seconds: Double, @Optional broadcast: Boolean?, @Optional broadcastInActionBar: Boolean?): Boolean {
        return isReady(player, enchant.basicData.id, seconds, broadcast, broadcastInActionBar)
    }

    @Export
    fun addCooldown(player: Player, key: String) {
        player.addCd(key)
    }

    @Export
    fun addCooldown(player: Player, enchant: AiyatsbusEnchantment) {
        addCooldown(player, enchant.basicData.id)
    }

    @Export
    fun removeCooldown(player: Player, key: String) {
        player.removeCd(key)
    }

    @Export
    fun removeCooldown(player: Player, enchant: AiyatsbusEnchantment) {
        removeCooldown(player, enchant.basicData.id)
    }

    @Export
    fun clearCooldown(player: Player) {
        player.clearCd()
    }
}