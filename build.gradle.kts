import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    id("fabric-loom") version "1.11-SNAPSHOT"
    id("maven-publish")
    id("com.gradleup.shadow") version "9.0.2"
    kotlin("jvm") version "2.2.10"
}

fun buildMetadata(): String {
    System.getenv("BUILD_METADATA")?.let { return it }
    System.getenv("GITHUB_RUN_NUMBER")?.let { return "build.${it}" }
    return "unknown"
}

version = "${project.property("mod_version")}-mc${project.property("compatible_mc_version")}+${buildMetadata()}"
group = "nk0.me"

base {
    archivesName = "redstone-comp-util"
}

loom {
    accessWidenerPath = file("src/main/resources/rcu.accesswidener")
}

tasks.withType<AbstractArchiveTask> {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("fabric_kotlin_version")}")

    val apiModules = setOf(
        "fabric-command-api-v2",
        "fabric-events-interaction-v0",
        "fabric-lifecycle-events-v1",
        "fabric-particles-v1"
    )
    apiModules.forEach {
        modImplementation(fabricApi.module(it, project.property("fabric_version") as? String))
    }

    implementation("com.google.code.gson:gson:${project.property("gson_version")}")
    implementation("com.google.guava:guava:${project.property("guava_version")}")
    implementation("io.netty:netty-all:${project.property("netty_version")}")
}

tasks.processResources {
    inputs.property("version", project.property("version"))

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to project.property("version"),
                "detailed_mc_version" to project.property("detailed_mc_version")
            )
        )
    }

    exclude("**/*.po")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 17
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    from("LICENSE") {
        rename { "${it}-rcu" }
    }

    dependencies {
        include(dependency("io.netty:netty-codec-http:.*"))
    }

    exclude("mappings/**")
    minimize()
}

tasks.remapJar {
    dependsOn(tasks.shadowJar)
    inputFile = file(tasks.shadowJar.get().archiveFile)
}

// extract I18n keys
// task("extract") {
//     doLast {
//         exec {
//             workingDir(layout.buildDirectory)
//             executable("bash")
//             args(
//                     "-c",
//                     "find ${projectDir}/src/main/java/ -iname \"*.java\"" +
//                             " | xargs xgettext -kI18n.t:1 -kI18n.t:2 -kI18n.overlay:2" +
//                             " -kI18n.overlayError:2 -kI18n.send:2 -kI18n.sendError:2")
//         }
//     }
// }
