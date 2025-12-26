repositories {
    mavenLocal()
}

dependencies {
    // 引入 API
    compileOnly(project(":project:common"))
    compileOnly("org.tabooproject.fluxon:core:1.4.0")
}

// 子模块
taboolib { subproject = true }