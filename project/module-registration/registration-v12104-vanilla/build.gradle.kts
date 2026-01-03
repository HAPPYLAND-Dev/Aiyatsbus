dependencies {
    compileOnly(project(":project:module-registration:registration-v12104-paper"))
    compileOnly("ink.ptms.core:v12111:12111:mapped")
    compileOnly("paper:v12107:12107:core")
    // Mojang API
    compileOnly("com.mojang:brigadier:1.0.18")
}

// 编译配置
java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// 子模块
taboolib { subproject = true }