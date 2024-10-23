plugins {
    id("com.gradleup.shadow")
}

subprojects {
    group = "net.odinmc.essentials"
    version = "1.0-SNAPSHOT"

    apply(plugin = "java")
    apply(plugin = "java-library")
}
