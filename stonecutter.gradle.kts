plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.5-fabric"

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) { 
    group = "project"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("chiseledBuildAndCollect", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

gradle.beforeProject {
    val gitDir = rootDir.resolve(".git")
    if (gitDir.exists() && gitDir.isDirectory) {
        val hooksDir = gitDir.resolve("hooks")
        val preCommitHook = hooksDir.resolve("pre-commit")

        if (!preCommitHook.exists()) {
            hooksDir.mkdirs()
            preCommitHook.writeText(
                """
                |#!/bin/bash
                |
                |echo "Setting Stonecutter Branch To VCS Branch"
                |./gradlew "Reset active project"
                |
                """.trimMargin()
            )
            preCommitHook.setExecutable(true)
            println("Git pre-commit hook installed.")
        } else {
            println("Git pre-commit hook already exists. Skipping.")
        }
    } else {
        println("Not a Git repository. Skipping hook installation.")
    }
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
    }
}