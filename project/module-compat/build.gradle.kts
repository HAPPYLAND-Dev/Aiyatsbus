repositories {
    maven("https://repo.codemc.org/repository/maven-public/") // BentoBox
    maven("https://repo.essentialsx.net/releases/") // EssentialsX
}

dependencies {
    // 引入 API
    compileOnly(project(":project:common"))
    compileOnly("world.bentobox:bentobox:2.1.0-SNAPSHOT") // BentoBox
    compileOnly("public:Residence:minimize") // Residence, 为维护作者权益已去除所有逻辑代码
    compileOnly("public:CMILib:1.4.7.2:minimize") // CMILib, 为维护作者权益已去除所有逻辑代码
    compileOnly("net.essentialsx:EssentialsX:2.19.7") // EssentialsX
    compileOnly("public:QuickShop-Hikari:6.2.0.6") // QuickShop-Hikari
    compileOnly("public:QuickShop-Reremake:5.1.2.5") // QuickShop-Reremake
    compileOnly("net.citizensnpcs:citizensapi:2.0.37-SNAPSHOT") // Citizens
    compileOnly("ink.ptms:um:1.0.9")
    compileOnly("me.xiaozhangup:SlimeCargoNext:1.0.0:api") // SlimeCargoNext
    compileOnly("me.xiaozhangup:OrangDomain:1.0.0:api") // OrangDomain
}

// 子模块
taboolib { subproject = true }