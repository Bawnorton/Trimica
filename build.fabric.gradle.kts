@file:Suppress("UnstableApiUsage")

import dev.kikugie.fletching_table.annotation.MixinEnvironment
import trimica.utils.*

plugins {
    kotlin("jvm")
    id("trimica.common")
    id("fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.13"
}

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }

    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://maven.blamejared.com/")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.parchmentmc.org")

    strictMaven("https://www.cursemaven.com", "Curseforge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

val minecraft: String by project
val loader: String by project
base.archivesName = "${mod("id")}-${mod("version")}+$minecraft-$loader"

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        officialMojangMappings()
        deps("parchment") {
            parchment("org.parchmentmc.data:parchment-$it@zip")
        }
    })

    modImplementation("net.fabricmc:fabric-loader:0.16.14")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${deps("fabric_api")}")

    remoteDepBuilder(project, fletchingTable::modrinth)
        .dep("advanced-netherite") { modRuntimeOnly(it) }
        .dep("sodium") { modImplementation(it) }
        .dep("iris") {
            modRuntimeOnly(it)
            runtimeOnly("org.antlr:antlr4-runtime:4.13.1")
            runtimeOnly("io.github.douira:glsl-transformer:2.0.1")
            runtimeOnly("org.anarres:jcpp:1.4.14")
        }
        .dep("elytra-trims") { it ->
            modImplementation(it)
            deps("fabric-language-kotlin") {
                modRuntimeOnly("net.fabricmc:fabric-language-kotlin:$it")
            }
        }

    deps("jei") {
        val (mc, version) = it.split(':')
        modCompileOnly("mezz.jei:jei-$mc-$loader-api:$version")
        modImplementation("mezz.jei:jei-$mc-$loader:$version")
    }
    deps("rei") {
        modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-$loader:$it")
        modCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-$loader:$it")
//        runtimeOnly("me.shedaniel:RoughlyEnoughItems-$loader:$it")
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/$minecraft.accesswidener"))

    fabricApi {
        configureDataGeneration {
            createRunConfiguration = true
            client = true
            modId = "trimica"
            outputDirectory = rootProject.file("src/main/generated")
        }

        configureTests {
            enableGameTests = false
            eula = true
            clearRunDirectory = false
        }
    }

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
        appendProjectPathToConfigName = false
    }

    runConfigs["client"].apply {
        programArgs("--username=Bawnorton", "--uuid=17c06cab-bf05-4ade-a8d6-ed14aaf70545")
        name = "Fabric Client $minecraft"
    }

    runConfigs["server"].apply {
        name = "Fabric Server $minecraft"
    }

    runConfigs["clientGameTest"].apply {
        name = "Fabric Client Game Test $minecraft"
    }

    runConfigs["datagen"].apply {
        name = "Fabric Data Generation $minecraft"
    }

    afterEvaluate {
        runs.configureEach {
            applyMixinDebugSettings(::vmArg, ::property)
        }
    }
}

fletchingTable {
    fabric {
        entrypointMappings.put("fabric-datagen", "net.fabricmc.fabric.api.datagen.v1.FabricDataGeneratorEntrypoint")
        entrypointMappings.put("fabric-client-gametest", "net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest")
    }

    mixins.register("main") {
        mixin("default", "trimica.mixins.json")
        mixin("client", "trimica-client.mixins.json") {
            environment = MixinEnvironment.Env.CLIENT
        }
    }
}

tasks {
    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }

    processResources {
        exclude("META-INF/neoforge.mods.toml")
        exclude { it.name.endsWith("-accesstransformer.cfg") }
    }
}