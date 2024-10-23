repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    api("org.apache.commons:commons-lang3:3.12.0")
    api("com.zaxxer:HikariCP:5.0.1")
    api("org.redisson:redisson:3.22.0")
    api("org.apache.commons:commons-compress:1.26.0")
    api("commons-io:commons-io:2.14.0")
    api("com.google.guava:guava:32.0.0-android")
    api("com.github.ben-manes.caffeine:caffeine:3.1.8")
    api("org.jetbrains:annotations:23.0.0")
    api("com.google.code.gson:gson:2.10.1")
    api("org.spongepowered:configurate-gson:4.1.2")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}
