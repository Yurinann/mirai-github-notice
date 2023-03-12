plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.14.0"
}

group = "com.hcyacg"
version = "1.6.1"

repositories {
    if (System.getenv("CI")?.toBoolean() != true) {
        maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    }
    mavenCentral()
}

dependencies {
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("com.squareup.okhttp3:okhttp:4.2.2")
    implementation("com.alibaba:fastjson:1.2.76")
    implementation(kotlin("stdlib-jdk8"))
}

mirai {
    jvmTarget = JavaVersion.VERSION_17
}