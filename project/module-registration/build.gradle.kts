subprojects {
    dependencies {
        // 引入 API
        compileOnly(project(":project:common"))
        compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    }
}

tasks {
    jar {
        // 打包子项目源代码
        subprojects.forEach { from(it.sourceSets["main"].output) }
    }
    sourcesJar {
        // 打包子项目源代码
        subprojects.forEach { from(it.sourceSets["main"].allSource) }
    }
}


// 子模块
taboolib { subproject = true }