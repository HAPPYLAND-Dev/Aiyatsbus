package cc.polarastrum.aiyatsbus.core.data.trigger

import cc.polarastrum.aiyatsbus.core.Aiyatsbus
import cc.polarastrum.aiyatsbus.core.AiyatsbusEnchantment
import cc.polarastrum.aiyatsbus.core.AiyatsbusSettings
import cc.polarastrum.aiyatsbus.core.script.ScriptType
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.function.warning
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XSound
import taboolib.library.xseries.particles.XParticle
import java.util.Optional

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.core.data.trigger.Skill
 *
 * @author mical
 * @since 2026/1/27 17:00
 */
data class Skill @JvmOverloads constructor(
    private val root: ConfigurationSection,
    private val enchant: AiyatsbusEnchantment,
    val scriptType: ScriptType = ScriptType.valueOf(root.getString("type")?.uppercase() ?: "KETHER"),
    val handle: String = root.getString("handle") ?: "",
    private val sound: Optional<XSound> = XSound.of(root.getString("sound") ?: ""),
    private val particleType: Optional<XParticle> = XParticle.of(root.getString("particle.type")),
    private val particleAmount: Int = root.getInt("particle.amount", 1),
    private val shiftNeeded: Boolean? = "shift-needed".let { if (root.contains(it)) root.getBoolean(it) else null },
    private val shiftIgnored: Boolean? = "shift-ignored".let { if (root.contains(it)) root.getBoolean(it) else null },
    private val action: ActionType? = root.getString("action")?.uppercase()?.let { ActionType.valueOf(it) },
    private val cooldownVar: String? = root.getString("cooldown.name"), // 这里是变量名
    private val sendCooldown: Boolean? = "cooldown.enable".let { if (root.contains(it)) root.getBoolean(it) else null },
    val priority: Int = root.getInt("priority", 0) // 按理来说应该是只有一个技能
) {

    private val internalId: String =
        "Enchantment_" + enchant.basicData.id + "_Skill_" + root.name.replace("-", "_")

    init {
        if (AiyatsbusSettings.enableKetherPreheat) {
            try {
                Aiyatsbus.api().getScriptHandler().getScriptHandler(scriptType).preheat(handle, internalId)
            } catch (ex: Throwable) {
                warning("Unable to preheat the skill ${root.name} of enchantment ${enchant.id}: $ex")
            }
        }
    }

    /**
     * 执行事件脚本
     *
     * 使用指定的脚本处理器执行事件处理脚本。
     *
     * @param sender 命令发送者
     * @param vars 变量映射表
     *
     * @example
     * ```kotlin
     * executor.execute(player, mapOf("level" to 5, "damage" to 10.0))
     * ```
     */
    fun execute(sender: CommandSender, vars: Map<String, Any?>) {
        Aiyatsbus.api().getScriptHandler().getScriptHandler(scriptType).invoke(handle, internalId, sender, vars)
    }

    fun isShiftNeeded(): Boolean {
        return shiftNeeded ?: Aiyatsbus.api().getSkillHandler().getSettings().shiftNeeded
    }

    fun isShiftIgnored(): Boolean {
        return shiftIgnored ?: Aiyatsbus.api().getSkillHandler().getSettings().shiftIgnored
    }

    fun getAction(): ActionType {
        return action ?: Aiyatsbus.api().getSkillHandler().getSettings().action
    }

    fun isEnableCooldown(): Boolean {
        return sendCooldown ?: Aiyatsbus.api().getSkillHandler().getSettings().enableCooldown
    }

    fun getCooldownVar(): String {
        return cooldownVar ?: Aiyatsbus.api().getSkillHandler().getSettings().cooldownVar
    }

    fun playSound(player: Player) {
        sound.ifPresent { it.play(player, 100f, 10f) }
    }

    fun spawnParticle(player: Player) {
        particleType.ifPresent {
            player.spawnParticle(
                it.get() ?: return@ifPresent,
                player.location.add(0.0, 2.0, 0.0),
                particleAmount
            )
        }
    }
}

enum class ActionType {
    RIGHT_CLICK, LEFT_CLICK, SWAP
}