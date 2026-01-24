plugins {
    kotlin("jvm") version "2.1.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io")}
}

dependencies {
    implementation("it.skrape:skrapeit:1.2.2")
    testImplementation(kotlin("test"))
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
    implementation("com.google.genai:google-genai:1.36.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.0")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.slf4j:slf4j-api:2.0.9")
}

application {
    mainClass.set("MainKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

