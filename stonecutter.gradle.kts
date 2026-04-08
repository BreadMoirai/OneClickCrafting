plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT" apply false
}

stonecutter active "26.1"

// Transforms JDK 22+ unnamed variables (_) in the shared src/ when switching versions.
// Forward (toUnnamed=false): replaces standalone _ with unused1, unused2, ... (per file)
// Reverse (toUnnamed=true):  replaces unusedN back to _
fun transformUnnamedVars(toUnnamed: Boolean) {
    val unnamedPattern = Regex("""\b_\b""")
    val renamedPattern = Regex("""\bunused\d+\b""")
    listOf("src/main/java", "src/test/java").forEach { srcPath ->
        val dir = rootProject.file(srcPath)
        if (!dir.exists()) return@forEach
        dir.walkTopDown().filter { it.isFile && it.extension == "java" }.forEach { file ->
            val original = file.readText()
            val transformed = if (toUnnamed) {
                renamedPattern.replace(original, "_")
            } else {
                var counter = 0
                unnamedPattern.replace(original) { "unused${++counter}" }
            }
            if (transformed != original) file.writeText(transformed)
        }
    }
}

tasks.register("transformUnnamedVars") {
    group = "stonecutter"
    description = "Replaces standalone _ with unused1, unused2, ... for older Java versions"
    notCompatibleWithConfigurationCache("transforms source files in place")
    doLast { transformUnnamedVars(toUnnamed = false) }
}

tasks.register("restoreUnnamedVars") {
    group = "stonecutter"
    description = "Restores unusedN identifiers back to JDK 22+ unnamed variable (_)"
    notCompatibleWithConfigurationCache("transforms source files in place")
    doLast { transformUnnamedVars(toUnnamed = true) }
}

afterEvaluate {
    tasks.findByName("stonecutterSwitchTo1.21.10")?.finalizedBy("transformUnnamedVars")
    tasks.findByName("stonecutterSwitchTo1.21.11")?.finalizedBy("transformUnnamedVars")
    tasks.findByName("stonecutterSwitchTo26.1")?.finalizedBy("restoreUnnamedVars")
}