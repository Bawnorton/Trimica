import dev.kikugie.fletching_table.annotation.MixinEnvironment
import trimica.utils.*

plugins {
  kotlin("jvm")
  `maven-publish`
  id("net.neoforged.moddev")
  id("trimica.common")
  id("me.modmuss50.mod-publish-plugin")
  id("com.google.devtools.ksp") version "2.2.0-2.0.2"
  id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.22"
  id("com.github.gmazzo.buildconfig") version "5.7.1"
}

repositories {
  fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
    forRepository { maven(url) { name = alias } }
    filter { groups.forEach(::includeGroup) }
  }

  maven("https://maven.bawnorton.com/releases")
  maven("https://maven.quiltmc.org/repository/release/")
  maven("https://maven.blamejared.com/")
  maven("https://maven.shedaniel.me/")
  maven("https://thedarkcolour.github.io/KotlinForForge/")
  maven("https://maven.parchmentmc.org")

  strictMaven("https://www.cursemaven.com", "Curseforge", "curse.maven")
  strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

val minecraft: String by project
val loader: String by project
base.archivesName = "${mod("id")}-${mod("version")}+$minecraft-$loader"

dependencies {
  remoteDepBuilder(project, fletchingTable::modrinth)
    .dep("advanced-netherite") { runtimeOnly(it) }
    .dep("elytra-trims") { it ->
      compileOnly(it)
//      deps("kotlinforforge-neoforge") {
//        runtimeOnly("thedarkcolour:kotlinforforge-neoforge:$it")
//      }
    }

  deps("sodium_internal") {
    val sodiumArtifact = "maven.modrinth:sodium:${deps("sodium")}"
    listOf(
      "sodium-neo-jar" to "META-INF/jarjar/net.caffeinemc.sodium-$it-mod.jar",
      "sodium-fapi-jar" to "META-INF/jarjar/fabric-api-base-${deps("sodium_fabric_api_base")}.jar",
      "sodium-frapi-jar" to "META-INF/jarjar/fabric-renderer-api-${deps("sodium_fabric_renderer")}.jar"
    ).forEach { (attrName, nestedPath) ->
      registerTransform(ExtractNestedJar::class.java) {
        from.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.JAR_TYPE)
        to.attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, attrName)
        parameters { nestedJarPath.set(nestedPath) }
      }
      compileOnly(sodiumArtifact) {
        attributes { attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, attrName) }
      }
    }
  }

  JvmVendorSpec.JETBRAINS

  deps("sodium") {
    compileOnly("maven.modrinth:sodium:$it")
  }

  deps("jei") {
    val (mc, version) = it.split(':')
    compileOnly("mezz.jei:jei-$mc-$loader-api:$version")
    compileOnly("mezz.jei:jei-$mc-$loader:$version")
  }
  deps("rei") {
    compileOnly("me.shedaniel:RoughlyEnoughItems-api-$loader:$it")
    compileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-$loader:$it")
//        runtimeOnly("me.shedaniel:RoughlyEnoughItems-$loader:$it")
  }
  deps("configurable") {
    implementation(annotationProcessor("com.bawnorton.configurable:configurable-$loader:$it")!!)
  }
  deps("bettertrims") {
    implementation("com.bawnorton.bettertrims:bettertrims-$loader:$it")
  }
}

java {
  withSourcesJar()
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

neoForge {
  version = deps("neoforge")

  validateAccessTransformers = true
  accessTransformers.from(rootProject.file("src/main/resources/$minecraft-accesstransformer.cfg"))

  mods {
    register(mod("id")!!) {
      sourceSet(sourceSets["main"])
    }
  }

  deps("parchment") {
    parchment {
      val (mc, version) = it.split(':')
      mappingsVersion = version
      minecraftVersion = mc
    }
  }

  runs {
    all {
      gameDirectory = rootProject.file("run")
    }

    register("client") {
      ideName = "NeoForge Client $minecraft"
      client()

      programArgument("--username=Bawnorton")
      programArgument("--uuid=17c06cab-bf05-4ade-a8d6-ed14aaf70545")
    }

    register("serverData") {
      ideName = "NeoForge Server Datagen $minecraft"
      serverData()
      programArguments.addAll(
        "--mod", "${mod("id")}",
        "--output", project.file("src/main/generated").toString()
      )
    }

    register("clientData") {
      ideName = "NeoForge Client Datagen $minecraft"
      clientData()
      programArguments.addAll(
        "--mod", "${mod("id")}",
        "--output", project.file("src/main/generated").toString()
      )
    }

    register("server") {
      ideName = "NeoForge Server $minecraft"
      server()
    }

  }

  afterEvaluate {
    runs.configureEach {
      applyMixinDebugSettings(::jvmArgument, ::systemProperty)
    }
  }
}

fletchingTable {
  mixins.register("main") {
    mixin("default", "trimica.mixins.json")
    mixin("client", "trimica.client.mixins.json") {
      environment = MixinEnvironment.Env.CLIENT
    }
  }
}

sourceSets.main {
  resources.srcDir(project.file("src/main/generated"))
  resources.exclude(".cache")
}

tasks {
  named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
  }

  register<Copy>("buildAndCollect") {
    group = "build"
    from(jar.map { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    dependsOn("build")
  }

  build {
    dependsOn("runServerData", "runClientData")
  }

  processResources {
    exclude("fabric.mod.json", "trimica.fabric.mixins.json")
    exclude { it.name.endsWith(".accesswidener") }
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
  file = tasks.jar.map { it.archiveFile.get() }
  additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })

  displayName = "${mod("name")} Neoforge ${mod("version")} for $minecraft"
  version = mod("version")
  changelog = provider { rootProject.file("CHANGELOG.md").readText() }
  modLoaders.add(loader)

  val compatibleVersionString = mod("compatible_versions")!!
  val compatibleVersions = compatibleVersionString.split(",").map { it.trim() }

  modrinth {
    projectId = property("publishing.modrinth") as String
    accessToken = mrToken
    minecraftVersions.addAll(compatibleVersions)
    requires("configurable")
  }

  curseforge {
    projectId = property("publishing.curseforge") as String
    accessToken = cfToken
    minecraftVersions.addAll(compatibleVersions)
    requires("configurable")
  }
}