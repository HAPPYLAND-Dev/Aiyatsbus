package cc.polarastrum.aiyatsbus.module.script.fluxon.relocate

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import taboolib.common.TabooLib
import taboolib.common.platform.function.debug
import taboolib.common.util.execution
import taboolib.common.util.t

/**
 * Aiyatsbus
 * cc.polarastrum.aiyatsbus.module.script.fluxon.relocate.AsmClassTranslation
 *
 * @author mical
 * @since 2026/1/3 14:05
 */
object AsmClassTranslation {

    const val PACKAGE_BEFORE = "cc/polarastrum/aiyatsbus/module/script/fluxon/library/fluxon"
    const val PACKAGE_AFTER = "org/tabooproject/fluxon"

    // 自定义 ClassLoader，使用原始 ClassLoader 作为 parent
    private class TranslationClassLoader(parent: ClassLoader) : ClassLoader(parent) {
        fun defineClass(name: String, bytes: ByteArray): Class<*> {
            return defineClass(name, bytes, 0, bytes.size)
        }
    }

    private val classLoader = TranslationClassLoader(AsmClassTranslation::class.java.classLoader)

    @Synchronized
    fun createNewClass(source: String): Class<*> {
        var inputStream = AsmClassTranslation::class.java.classLoader.getResourceAsStream(source.replace('.', '/') + ".class")
        if (inputStream == null) {
            inputStream = TabooLib::class.java.classLoader.getResourceAsStream(source.replace('.', '/') + ".class")
        }
        if (inputStream == null) {
            error(
                """
                    没有找到将被转译的类 $source
                    No class found to be translated $source
                """.t()
            )
        }
        val bytes = inputStream.readBytes()
        // 转译
        val (newClass, cost2) = execution {
            val classReader = ClassReader(bytes)
            val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
            // 创建带类名修改的 Remapper
            val renameRemapper = RelocateTranslation(PACKAGE_BEFORE, PACKAGE_AFTER, source.replace('.', '/'))
            // 转译器
            classReader.accept(ClassRemapper(classWriter, renameRemapper), 0)
            // 使用自定义 ClassLoader 定义类
            classLoader.defineClass("${source}T", classWriter.toByteArray())
        }
        debug("[AsmClassTranslation] 转译 $source，用时 $cost2 毫秒。")
        return newClass
    }
}