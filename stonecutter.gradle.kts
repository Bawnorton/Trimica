
plugins {
    kotlin("jvm") version "2.2.0" apply false
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.11-SNAPSHOT" apply false
    id("net.neoforged.moddev") version "2.0.95" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.+" apply false
}

stonecutter active "1.21.8-fabric"

stonecutter parameters {
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge")
    listOf("jei", "rei").forEach {
        constants[it] = node.project.hasProperty("deps.$it")
    }
}