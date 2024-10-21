import com.diffplug.spotless.LineEnding

plugins {
    id("com.gradleup.shadow")
    id("com.diffplug.spotless")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
}

spotless {
    lineEndings = LineEnding.UNIX
    isEnforceCheck = false

    val prettierConfig =
        mapOf(
            "prettier" to "2.8.8",
            "prettier-plugin-java" to "2.1.0",
        )

    format("encoding") {
        target("*.*")
        encoding("UTF-8")
        endWithNewline()
        trimTrailingWhitespace()
    }

    yaml {
        target(
            "**/src/main/resources/*.yaml",
            "**/src/main/resources/*.yml",
            ".github/**/*.yml",
            ".github/**/*.yaml",
        )
        endWithNewline()
        trimTrailingWhitespace()
        val jackson = jackson()
        jackson.yamlFeature("LITERAL_BLOCK_STYLE", true)
        jackson.yamlFeature("SPLIT_LINES", false)
    }

    kotlinGradle {
        target("**/*.gradle.kts")
        indentWithSpaces(2)
        endWithNewline()
        trimTrailingWhitespace()
        ktlint()
    }

    java {
        target("**/src/**/java/**/*.java")
        importOrder()
        removeUnusedImports()
        indentWithSpaces(4)
        endWithNewline()
        trimTrailingWhitespace()
        prettier(prettierConfig)
            .config(
                mapOf("parser" to "java", "tabWidth" to 4, "useTabs" to false, "printWidth" to 150),
            )
    }
}
