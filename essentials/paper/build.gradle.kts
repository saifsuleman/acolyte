plugins {
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

repositories {
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    compileOnly(project(":core:paper"))
}
