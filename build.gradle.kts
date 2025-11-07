plugins {
    kotlin("jvm") version "2.0.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.java.dev.jna:jna:5.12.1")
}

application {
    mainClass.set("phizz.MainKt")
    applicationDefaultJvmArgs = listOf("-Djna.library.path=/opt/homebrew/lib")
}
