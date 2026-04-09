plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("maven-publish")
}

version = "${property("mod.version")}+${sc.current.version}"
group = property("mod.group") as String

base.archivesName = property("mod.id") as String

val requiredJava = when {
    sc.current.parsed >= "26" -> 25
    sc.current.parsed >= "1.20.5" -> 21
    sc.current.parsed >= "1.18" -> 17
    sc.current.parsed >= "1.17" -> 16
    else -> 8
}
java.toolchain.languageVersion.set(JavaLanguageVersion.of(requiredJava))

repositories {
    maven("https://maven.gegy.dev") {
        name = "Gegy"
    }
    maven("https://maven.terraformersmc.com/releases/") {
        name = "TerraformersMC"
    }
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

    modImplementation("com.terraformersmc:modmenu:${property("modmenu_version")}")
    modImplementation("dev.isxander:yet-another-config-lib:${property("yacl_version")}")

    testImplementation(sourceSets.main.get().output)
}

tasks {
    processResources {
        notCompatibleWithConfigurationCache("I don't know why...")
        inputs.property("version", project.property("mod.version"))
        inputs.property("minecraft", project.property("minecraft_version_range"))
        inputs.property("yacl", project.property("yacl_version"))
        inputs.property("modmenu", project.property("modmenu_version"))

        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                "version" to project.property("mod.version"),
                "minecraft" to project.property("minecraft_version_range"),
                "yacl" to project.property("yacl_version").apply { this.toString().substringBefore('-') },
                "modmenu" to project.property("modmenu_version").apply { this.toString().substringBefore('-') }))
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile }, remapSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("remapJar")
        dependsOn("remapSourcesJar")
    }
}


// When building old versions without switching first, transform _ in generated sources.
// Stonecutter generates sources from shared src/ (which may contain JDK 22+ unnamed _).
if (sc.current.parsed < "26") {
    val transformForBuild = tasks.register("transformUnnamedVarsForBuild") {
        group = "stonecutter"
        description = "Replaces standalone _ with unused1, unused2, ... in Stonecutter generated sources"
        notCompatibleWithConfigurationCache("transforms generated source files in place")
        @Suppress("OPT_IN_USAGE")
        dependsOn(sc.tasks.generate.values)
        doLast {
            val unnamedPattern = Regex("""\b_\b""")
            val genDir = sc.tasks.generatedSourcesDir.get().asFile
            genDir.walkTopDown().filter { it.isFile && it.extension == "java" }.forEach { file ->
                val original = file.readText()
                var counter = 0
                val transformed = unnamedPattern.replace(original) { "unused${++counter}" }
                if (transformed != original) file.writeText(transformed)
            }
        }
    }
    tasks.withType<JavaCompile>().configureEach { dependsOn(transformForBuild) }
}

sourceSets {
    named("test") {
        compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.test.get().compileClasspath
        runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.test.get().runtimeClasspath
    }
}

val testTasks = listOf("runTestClient", "compileTestJava")
val runningTests = gradle.startParameter.taskNames.any { name -> testTasks.any { name.endsWith(it) } }

loom {
    accessWidenerPath.set {
        return@set if (runningTests) file("src/test/resources/oneclickcraftingtestmod.accesswidener")
        else file("src/main/resources/oneclickcrafting.accesswidener")
    }

    runs {
        register("TestClient") {
            client()
            name("Test Client")
            source(sourceSets.test.get())
            vmArgs("-Dfabric.client.gametest", "-Dfabric.client.gametest.disableNetworkSynchronizer")
        }
    }

    createRemapConfigurations(sourceSets.test.get())
}

apply(from = "../../stonecutter-swaps.gradle.kts")
@Suppress("UNCHECKED_CAST")
val swapMap = extra["swaps"] as Map<String, Map<String, String>>
stonecutter {
    replacements {
        for ((version, swaps) in swapMap) {
            string(sc.current.parsed >= version) {
                for ((from, to) in swaps) {
                    replace(from, to)
                }
            }
        }
    }
}