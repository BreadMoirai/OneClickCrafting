pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        maven("https://maven.terraformersmc.com/releases/")
    }
}

plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT" apply false
    id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
    create(rootProject) {
        // See https://stonecutter.kikugie.dev/wiki/start/#choosing-minecraft-versions
        versions("1.21.11")
        vcsVersion = "1.21.11"
    }
}

rootProject.name = "OneClickCrafting"