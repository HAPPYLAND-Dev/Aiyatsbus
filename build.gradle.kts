@file:Suppress("PropertyName", "SpellCheckingInspection")

import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.23" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.25" apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.izzel.taboolib")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    // TabooLib 配置
    // 这里的配置是全局的，如果你的项目有多个模块，这里的配置会被所有模块共享
    // 为了降低理解难度，使用这种更加无脑的配置方式
    configure<TabooLibExtension> {
        description {
            name(rootProject.name)
        }
        env {
            install(
                Bukkit,
                BukkitHook,
                BukkitNMSItemTag,
                BukkitUI,
                BukkitUtil,
                I18n,
                JavaScript,
                Kether,
                MinecraftChat,
                MinecraftEffect,
                Metrics
            )
        }
        version {
            taboolib = "6.2.3-12d4045"
        }
    }

    // 仓库
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.citizensnpcs.co/repo")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
    // 依赖
    dependencies {
        compileOnly(kotlin("stdlib"))
        compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    }

    // 编译配置
    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "21"
            freeCompilerArgs = listOf("-Xjvm-default=all", "-Xextended-compiler-checks")
        }
    }
}

gradle.buildFinished {
    buildDir.deleteRecursively()
}