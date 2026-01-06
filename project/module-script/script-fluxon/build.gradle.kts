repositories {
    mavenLocal()
}

dependencies {
    // 引入 API
    compileOnly(project(":project:common"))
    compileOnly("org.tabooproject.fluxon:core:1.4.7")
    compileOnly("org.tabooproject.fluxon.plugin:core:1.0.0-test-6")
    compileOnly("org.tabooproject.fluxon.plugin:platform-bukkit:1.0.0-test-6")
    // Reflex Remapper
    compileOnly("org.ow2.asm:asm:9.8")
    compileOnly("org.ow2.asm:asm-util:9.8")
    compileOnly("org.ow2.asm:asm-commons:9.8")
}

// 子模块
taboolib { subproject = true }