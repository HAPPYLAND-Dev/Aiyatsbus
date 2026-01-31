dependencies {
    // 引入 API
    compileOnly(project(":project:common"))
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    // 旧版本自定义附魔注册器
    compileOnly(project(":project:module-registration:registration-legacy"))
}

// 子模块
taboolib { subproject = true }