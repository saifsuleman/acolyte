plugins {
    id("com.gradleup.shadow")
}

subprojects {
    group = "net.odinmc.core"
    version = "1.0-SNAPSHOT"

    apply(plugin = "java")
    apply(plugin = "java-library")
}
