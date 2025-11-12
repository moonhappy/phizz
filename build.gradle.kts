import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm") version "2.0.0"
    application
    id("org.gradle.jacoco")
}

repositories {
    mavenCentral()
}

dependencies {
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // JNA
    implementation("net.java.dev.jna:jna:5.12.1")

    // Testing
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

application {
    mainClass.set("phizz.MainKt")
    applicationDefaultJvmArgs = listOf("-Djna.library.path=/opt/homebrew/lib")
}

tasks.run {
    standardInput = System.`in`
    if (project.hasProperty("appArgs")) {
        args = (project.property("appArgs") as String).split(" ")
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "phizz.MainKt"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
