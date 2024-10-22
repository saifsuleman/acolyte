pluginManagement {
    repositories {
        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven("https://libraries.minecraft.net/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        gradlePluginPortal()
    }

    plugins {
        id("com.gradleup.shadow") version("8.3.0")
        id("com.diffplug.spotless") version("6.21.0")
    }
}

rootProject.name = "odinmc"
include("core")
include("core:paper")
findProject(":core:paper")?.name = "paper"
include("core:common")
findProject(":core:common")?.name = "common"
