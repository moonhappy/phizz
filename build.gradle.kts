import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.nio.file.Files
import java.nio.file.Paths

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
    implementation(files("libs/libbluray/src/.libs/libbluray-j2se-1.4.0.jar"))
    implementation(files("libs/libbluray/src/.libs/libbluray-awt-j2se-1.4.0.jar"))

    // Testing
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

application {
    mainClass.set("phizz.MainKt")
    applicationDefaultJvmArgs = listOf("-Djna.library.path=${layout.buildDirectory.dir("resources/main/natives").get().asFile.absolutePath}")
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
    systemProperties["jna.library.path"] = layout.buildDirectory.dir("resources/main/natives").get().asFile.path
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

val buildLibdvdnav by tasks.registering(Exec::class) {
    description = "Builds the libdvdnav native library."
    group = "build"
    workingDir(layout.projectDirectory.dir("libs/libdvdnav"))
    
    inputs.dir(layout.projectDirectory.dir("libs/libdvdnav/src"))
    inputs.file(layout.projectDirectory.file("libs/libdvdnav/meson.build"))
    outputs.dir(layout.projectDirectory.dir("libs/libdvdnav/build/src"))

    commandLine(
        "sh", "-c",
        """
        set -e
        if [ ! -d "subprojects/libdvdread" ]; then
            echo "Cloning libdvdread..."
            git clone --branch master --depth 1 https://code.videolan.org/videolan/libdvdread.git subprojects/libdvdread
        fi
        
        rm -rf build
        echo "Configuring meson..."
        meson setup build
        
        echo "Compiling with meson..."
        meson compile -C build
        """
    )
}

val copyLibdvdnav by tasks.registering(Copy::class) {
    dependsOn(buildLibdvdnav)
    from(file("libs/libdvdnav/build/src")) {
        include("libdvdnav.so.*", "libdvdnav.*.dylib", "dvdnav.dll", "libdvdnav.dll")
    }
    into(layout.projectDirectory.dir("src/main/resources/natives"))
    doLast {
        val targetDir = layout.projectDirectory.dir("src/main/resources/natives").asFile
        val symlink = Paths.get(targetDir.absolutePath, "libdvdnav.dylib")
        val target = Paths.get(targetDir.absolutePath, "libdvdnav.4.dylib")
        if (Files.exists(target) && !Files.exists(symlink)) {
            Files.createSymbolicLink(symlink, target.fileName)
        }
    }
}

val buildLibbluray by tasks.registering(Exec::class) {
    description = "Builds the libbluray native library."
    group = "build"
    workingDir(layout.projectDirectory.dir("libs/libbluray"))

    inputs.dir(layout.projectDirectory.dir("libs/libbluray/src"))
    inputs.file(layout.projectDirectory.file("libs/libbluray/meson.build"))
    outputs.dir(layout.projectDirectory.dir("libs/libbluray/build/src"))

    commandLine(
        "sh", "-c",
        """
        set -e
        rm -rf build
        echo "Configuring meson for libbluray..."
        meson setup -Dlibxml2=enabled build
        
        echo "Compiling with meson for libbluray..."
        meson compile -C build
        """
    )
}

val copyLibbluray by tasks.registering(Copy::class) {
    dependsOn(buildLibbluray)
    from(file("libs/libbluray/build/src")) {
        include("libbluray.so.*", "libbluray.*.dylib", "bluray.dll", "libbluray.dll")
    }
    into(layout.projectDirectory.dir("src/main/resources/natives"))
    doLast {
        val targetDir = layout.projectDirectory.dir("src/main/resources/natives").asFile
        val symlink = Paths.get(targetDir.absolutePath, "libbluray.dylib")
        val target = Paths.get(targetDir.absolutePath, "libbluray.3.dylib")
        if (Files.exists(target) && !Files.exists(symlink)) {
            Files.createSymbolicLink(symlink, target.fileName)
        }
    }
}

val buildLibblurayBdJ by tasks.registering(Exec::class) {
    description = "Builds the libbluray bd-j JARs."
    group = "build"
    workingDir(layout.projectDirectory.dir("libs/libbluray/src/libbluray/bdj"))
    commandLine(
        "ant", "-Dversion=j2se-1.4.0", "-Djava_version_asm=1.8", "-Djava_version_bdj=1.8", "-Dsrc_awt=:java-j2se:java-build-support"
    )
}

tasks.named("compileKotlin") {
    dependsOn(buildLibblurayBdJ)
}

tasks.named("processResources") {
    dependsOn(copyLibdvdnav, copyLibbluray)
}
