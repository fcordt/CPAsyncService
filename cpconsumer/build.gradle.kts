val logbackVersion: String by project
val kafkaVersion: String by project
val ktorVersion: String by project
val kotlinxVersion: String by project
val mongoVersion: String by project
val datetimeVersion: String by project
val serializationVersion: String by project

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
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongoVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:$datetimeVersion")
    //using kotlinx's datetime with mongodb: https://stackoverflow.com/questions/77851362/how-to-use-kotlinx-datetime-with-kotlin-mongodb-driver
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
    implementation("org.mongodb:bson-kotlinx:$mongoVersion")
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