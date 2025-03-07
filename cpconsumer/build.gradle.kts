val logback_version: String by project
val kafka_version: String by project
val ktor_version: String by project
val kotlinx_version: String by project
val mongo_version: String by project
val datetime_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    application
    kotlin("plugin.serialization") version "2.1.10"
    id("com.bmuschko.docker-java-application") version "9.4.0"
}

application {
    mainClass.set("at.fcordt.cpconsumer.MainKt")
}

group = "at.fcordt"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.apache.kafka:kafka-clients:$kafka_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongo_version")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:$datetime_version")
}

kotlin {
    jvmToolchain(17)
}

docker {
    javaApplication {
        baseImage.set("bellsoft/liberica-openjdk-alpine:17")
        mainClassName = application.mainClass
    }
}