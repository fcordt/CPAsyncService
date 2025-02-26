val kotlin_version: String by project
val logback_version: String by project
val kafka_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
}

group = "at.fcordt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.apache.kafka:kafka-clients:$kafka_version")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}