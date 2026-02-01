dependencies {
    compileOnly(project(":project:common"))
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("ink.ptms.core:v12002:12002:mapped")
}

// 子模块
taboolib { subproject = true }