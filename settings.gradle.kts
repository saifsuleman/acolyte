pluginManagement {
    repositories {
        maven(url = "https://repo.papermc.io/repository/maven-public/")
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
