/*
 *  Copyright (C) 2022-2024 PolarAstrumLab 坏黑
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
package cc.polarastrum.aiyatsbus.module.script.fluxon

import org.tabooproject.fluxon.runtime.FluxonRuntime
import org.tabooproject.fluxon.runtime.FluxonRuntimeError
import org.tabooproject.fluxon.runtime.Function
import org.tabooproject.fluxon.runtime.error.ArgumentTypeMismatchError
import org.tabooproject.fluxon.runtime.error.FunctionNotFoundError
import org.tabooproject.fluxon.runtime.error.IndexAccessError
import org.tabooproject.fluxon.runtime.error.VariableNotFoundError
import taboolib.common.platform.function.warning
import java.util.concurrent.CompletableFuture
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * 处理 CompletableFuture 中的 FluxonRuntimeError 异常
 */
fun Any.exceptFluxonCompletableFutureError() {
    if (this is CompletableFuture<*>) {
        this.exceptionally { ex ->
            if (ex is FluxonRuntimeError) {
                ex.printError()
            }
            null
        }
    }
}

fun FluxonRuntimeError.printError() {
    warning(message)
    when (this) {
        // 参数类型错误
        is ArgumentTypeMismatchError -> {
            if (actual == null) {
                warning("函数 ${context.function.name} 的第 ${index + 1} 个参数为空")
            } else {
                warning("函数 ${context.function.name} 的第 ${index + 1} 个参数类型错误")
                warning("实际: ${actual.javaClass.simpleName} ($actual)")
            }
            warning("期望: ${expect.simpleName}")
        }
        // 函数未找到
        is FunctionNotFoundError -> {
            warning("没有找到函数: $name (pos: $pos,$exPos)")
            warning("参数: ${arguments.joinToString(", ")}")
            // 打印目标
            if (target != null) {
                warning("目标: $target (${target.javaClass})")
                val find = arrayListOf<Function>()
                FluxonRuntime.getInstance().extensionFunctions.forEach { (_, value) ->
                    value.forEach { (type, func) ->
                        if (type.isAssignableFrom(javaClass)) {
                            find += func
                        }
                    }
                }
                warning("目标支持的扩展函数: ${find.sortedBy { func -> func.name }.map { func -> func.name }}")
            } else {
                warning("目标为空")
            }
        }
        // 索引访问错误
        is IndexAccessError -> {
            warning("索引访问错误: $errorType")
            warning("目标: $target (${target.javaClass})")
            warning("索引: $index")
        }
        // 变量未找到
        is VariableNotFoundError -> {
            warning("没有找到变量: $variableName (index: $index)")
            warning("可用: $availableVariables")
        }
    }
}