dependencies {
    compileOnly(project(":project:module-registration:registration-v12103-paper"))
    compileOnly("ink.ptms.core:v12103:12103:mapped")
}

// 编译配置
java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// 子模块
taboolib { subproject = true }