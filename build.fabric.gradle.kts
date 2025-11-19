@file:Suppress("UnstableApiUsage")

import dev.kikugie.fletching_table.annotation.MixinEnvironment
import org.gradle.kotlin.dsl.remapJar
import trimica.utils.*

plugins {
  kotlin("jvm")
  `maven-publish`
  id("trimica.common")
  id("fabric-loom")
  id("me.modmuss50.mod-publish-plugin")
  id("com.google.devtools.ksp") version "2.2.0-2.0.2"
  id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
  id("com.github.gmazzo.buildconfig") version "5.7.1"
}

repositories {
  fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
    forRepository { maven(url) { name = alias } }
    filter { groups.forEach(::includeGroupAndSubgroups) }
  }

  maven("https://maven.bawnorton.com/releases")
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

  modImplementation("net.fabricmc:fabric-loader:0.17.3")
  modImplementation("net.fabricmc.fabric-api:fabric-api:${deps("fabric_api")}")

  remoteDepBuilder(project, fletchingTable::modrinth)
    .dep("advanced-netherite") { modRuntimeOnly(it) }
    .dep("oxidizable-copper-gear") { modRuntimeOnly(it) }
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
    modCompileOnly("mezz.jei:jei-$mc-$loader:$version")
  }
  deps("rei") {
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-$loader:$it")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-$loader:$it")
//        runtimeOnly("me.shedaniel:RoughlyEnoughItems-$loader:$it")
  }
  deps("configurable") {
    modImplementation(annotationProcessor("com.bawnorton.configurable:configurable-$loader:$it")!!)
  }
  deps("bettertrims") {
    modImplementation("com.bawnorton.bettertrims:bettertrims-$loader:$it")
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
    runConfigs.configureEach {
      applyMixinDebugSettings(::vmArg, ::property)
    }
  }
}

fletchingTable {
  fabric {
    entrypointMappings.put("elytratrims-client", "dev.kikugie.elytratrims.api.ETClientInitializer")
    entrypointMappings.put("fabric-datagen", "net.fabricmc.fabric.api.datagen.v1.FabricDataGeneratorEntrypoint")
    entrypointMappings.put("fabric-client-gametest", "net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest")
  }

  mixins.register("main") {
    mixin("default", "trimica.mixins.json")
    mixin("client", "trimica.client.mixins.json") {
      environment = MixinEnvironment.Env.CLIENT
    }
    mixin("fabric", "trimica.fabric.mixins.json")
  }
}

tasks {
  register<Copy>("buildAndCollect") {
    group = "build"
    from(remapJar.map { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    dependsOn("build")
  }

  remapJar {
    dependsOn("runDatagen")
  }

  named<Jar>("sourcesJar") {
    dependsOn("runDatagen")
  }

  processResources {
    exclude("META-INF/neoforge.mods.toml")
    exclude { it.name.endsWith("-accesstransformer.cfg") }
  }
}

buildConfig {
  className("TrimicaConstants")
  packageName("com.bawnorton.trimica")
  useJavaOutput()

  buildConfigField("String", "MINECRAFT_VERSION", "\"$minecraft\"")
  buildConfigField("String", "LOADER", "\"$loader\"")
  buildConfigField("String", "MOD_VERSION", "\"${mod("version")}\"")
  buildConfigField("int", "DATA_VERSION", mod("version")!!.replace(".", "").toInt())
}

extensions.configure<PublishingExtension> {
  repositories {
    maven {
      name = "bawnorton"
      url = uri("https://maven.bawnorton.com/releases")
      credentials(PasswordCredentials::class)
      authentication {
        create<BasicAuthentication>("basic")
      }
    }
  }
  publications {
    create<MavenPublication>("maven") {
      groupId = "${mod("group")}.${mod("id")}"
      artifactId = "${mod("id")}-$loader"
      version = "${mod("version")}+$minecraft"

      from(components["java"])
    }
  }
}

publishMods {
  val mrToken = providers.gradleProperty("MODRINTH_TOKEN")
  val cfToken = providers.gradleProperty("CURSEFORGE_TOKEN")

  type = BETA
  file = tasks.remapJar.map { it.archiveFile.get() }
  additionalFiles.from(tasks.remapSourcesJar.map { it.archiveFile.get() })

  displayName = "${mod("name")} Fabric ${mod("version")} for $minecraft"
  version = mod("version")
  changelog = provider { rootProject.file("CHANGELOG.md").readText() }
  modLoaders.add(loader)

  val compatibleVersionString = mod("compatible_versions")!!
  val compatibleVersions = compatibleVersionString.split(",").map { it.trim() }

  modrinth {
    projectId = property("publishing.modrinth") as String
    accessToken = mrToken
    minecraftVersions.addAll(compatibleVersions)
    requires("fabric-api", "configurable")
  }

  curseforge {
    projectId = property("publishing.curseforge") as String
    accessToken = cfToken
    minecraftVersions.addAll(compatibleVersions)
    requires("fabric-api", "configurable")
  }
}