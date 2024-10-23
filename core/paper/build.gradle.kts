plugins {
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

repositories {
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    api("org.incendo:cloud-paper:2.0.0-beta.10")
    api("org.incendo:cloud-minecraft-extras:2.0.0-beta.10")
    api("org.incendo:cloud-core:2.0.0")
    api("org.incendo:cloud-annotations:2.0.0")

    compileOnlyApi(project(":core:common"))
    compileOnlyApi("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}
