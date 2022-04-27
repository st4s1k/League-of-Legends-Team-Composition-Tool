import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*

plugins {
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    java
    `maven-publish`
    application
    id("org.openjfx.javafxplugin") version "0.0.12"
    id("com.gluonhq.gluonfx-gradle-plugin") version "1.0.13"
}


fun getLTCProperty(key: String): String {
    val properties = Properties()
    val filePath = "src/main/resources/com/st4s1k/leagueteamcomp/ltc.properties"
    val localProperties = File(filePath)
    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else error("File from not found")
    return properties.getProperty(key)
}

group = "com.st4s1k"
version = getLTCProperty("version")
description = "LeagueTeamComp"
java.sourceCompatibility = JavaVersion.VERSION_17

val ltcBuild by tasks.registering {
    println()
    println("  _                               _____                     ____                      ")
    println(" | |    ___  __ _  __ _ _   _  __|_   _|__  __ _ _ __ ___  / ___|___  _ __ ___  _ __  ")
    println(" | |   / _ \\/ _` |/ _` | | | |/ _ \\| |/ _ \\/ _` | '_ ` _ \\| |   / _ \\| '_ ` _ \\| '_ \\ ")
    println(" | |__|  __/ (_| | (_| | |_| |  __/| |  __/ (_| | | | | | | |__| (_) | | | | | | |_) |")
    println(" |_____\\___|\\__,_|\\__, |\\__,_|\\___||_|\\___|\\__,_|_| |_| |_|\\____\\___/|_| |_| |_| .__/ ")
    println("==================|___/========================================================|_|====")
    println(":: League of Legends Team Composition Tool ::" + String.format("%41s", String.format("(v%s)", version)))
    println()
    dependsOn("clean", "nativeRunAgent", "build", "nativeBuild", "nativeRun")
}

tasks {
    nativeRunAgent {
        shouldRunAfter(clean)
    }
    build {
        shouldRunAfter(nativeRunAgent)
    }
    nativeBuild {
        shouldRunAfter(build)
    }
    nativeRun {
        shouldRunAfter(nativeBuild)
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://nexus.gluonhq.com/nexus/content/repositories/releases")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    // Spring
    implementation("com.gluonhq:ignite-spring:1.2.2")
    implementation("org.springframework.boot:spring-boot-starter:2.6.6") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-json")
    }
    // JavaFX
    implementation("org.openjfx:javafx-controls:18")
    implementation("org.openjfx:javafx-fxml:18")
    implementation("org.controlsfx:controlsfx:11.1.1")
    // Logging
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")
    // Other
    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
    implementation("com.github.stirante:lol-client-java-api:1.2.5")
    implementation("com.merakianalytics.orianna:orianna:4.0.0-rc8")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("dev.failsafe:failsafe:3.2.3")
}

val applicationMainClass = "com.st4s1k.leagueteamcomp.LeagueTeamCompApplication"

application {
    mainClassName = applicationMainClass
    applicationDefaultJvmArgs = listOf(
            "--add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls",
            "--add-exports=com.gluonhq.attach.util/com.gluonhq.attach.util.impl=com.st4s1k.leagueteamcomp",
            "--add-exports=javafx.base/com.sun.javafx.event=ALL-UNNAMED",
            "--add-exports=com.gluonhq.attach.util/com.gluonhq.attach.util.impl=ALL-UNNAMED"
    )
}

javafx {
    version = "17"
    modules = listOf("javafx.controls", "javafx.fxml")

}

gluonfx {
    target = "host"
    isVerbose = true
    reflectionList = listOf(
            "com.st4s1k.leagueteamcomp.controller.LeagueTeamCompController",
            "com.st4s1k.leagueteamcomp.LeagueTeamCompApplication"
    )
    bundlesList = listOf("com.st4s1k.leagueteamcomp.ltc-view")
    compilerArgs = listOf(
            "--native-image-info",
            "--verbose",
            "--allow-incomplete-classpath"
    ) + application.applicationDefaultJvmArgs
}
