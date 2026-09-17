plugins {
    id("net.fabricmc.fabric-loom")
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
java.withSourcesJar()

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

    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

    implementation("com.terraformersmc:modmenu:${property("modmenu_version")}")
    implementation("dev.isxander:yet-another-config-lib:${property("yacl_version")}")

    testImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
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
                    "modmenu" to project.property("modmenu_version").apply { this.toString().substringBefore('-') })
            )
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Test> {
        failOnNoDiscoveredTests = false
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("jar")
    }
}


// When building 26.1 while shared src/ contains unusedN (switched from older version),
// restore them to JDK 22+ unnamed _ in generated sources before compilation.
val restoreForBuild = tasks.register("restoreUnnamedVarsForBuild") {
    group = "stonecutter"
    description = "Restores unusedN identifiers back to JDK 22+ unnamed variable (_) in generated sources"
    notCompatibleWithConfigurationCache("transforms generated source files in place")
    dependsOn(sc.tasks.generate.values)
    doLast {
        val renamedPattern = Regex("""\bunused\d+\b""")
        val genDir = sc.tasks.generatedSourcesDir.get().asFile
        genDir.walkTopDown().filter { it.isFile && it.extension == "java" }.forEach { file ->
            val original = file.readText()
            val transformed = renamedPattern.replace(original, "_")
            if (transformed != original) file.writeText(transformed)
        }
    }
}
tasks.withType<JavaCompile>().configureEach { dependsOn(restoreForBuild) }

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
        // MC 26.x on Windows (JDK 25+): pre-commit each thread's whole stack at thread start.
        // Without this, ~10-50% of client launches die in the first resource reload with
        // NTSTATUS 0xC0000005 / 0xC0000409 and no hs_err file: JIT-compiled code on a
        // Worker-Main thread stack-bangs past the Windows guard page, HotSpot's Windows fault
        // handler commits the stack range manually (leaving no guard page below it), and VM
        // call-site resolution (SharedRuntime::find_callee_*) then runs off the committed
        // region or corrupts its own frame. Pre-touching avoids that path entirely.
        // Verified 0/40 crashes vs 5/40+ without; see the update skill api-divergences.md.
        configureEach {
            vmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+AlwaysPreTouchStacks")
        }
        register("TestClient") {
            client()
            name("Test Client")
            source(sourceSets.test.get())
            vmArgs("-Dfabric.client.gametest", "-Dfabric.client.gametest.disableNetworkSynchronizer")
        }
    }
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
