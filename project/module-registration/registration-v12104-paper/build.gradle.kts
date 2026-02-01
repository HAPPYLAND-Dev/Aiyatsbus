dependencies {
    compileOnly("me.xiaozhangup.octopus:octopus-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("paper:v12107:12107:core")
    compileOnly(project(":project:common"))
}

// 编译配置
java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// 子模块
taboolib { subproject = true }