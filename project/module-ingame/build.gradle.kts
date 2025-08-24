dependencies {
    // 引入 API
    compileOnly(project(":project:common"))
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("ink.ptms.core:v12107:12107:mapped")
    compileOnly("com.github.retrooper:packetevents-spigot:2.9.4")
}

// 子模块
taboolib { subproject = true }