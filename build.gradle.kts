import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import net.fabricmc.loom.configuration.ide.RunConfigSettings
import net.neoforged.moddevgradle.dsl.RunModel

plugins {
    id("dev.isxander.modstitch.base") version "0.5.14-unstable"
}

fun deps(name: String): String? = findProperty("deps.${name}") as String?
fun deps(name: String, consumer: (prop: String) -> Unit) = deps(name)?.let(consumer)

val minecraft: String = deps("minecraft")!!
var loader: String = name.split("-")[1]

repositories {
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://api.modrinth.com/maven")
}

dependencies {
    modstitch.loom {
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${deps("fabric_api")}+1.21.5")
    }

    modstitch.moddevgradle {
    }

    modstitchModRuntimeOnly("maven.modrinth:sodium:mc$minecraft-${deps("sodium")}-$loader")
//    modstitchModRuntimeOnly("maven.modrinth:iris:${deps("iris")}+$minecraft-$loader")
    modstitchModRuntimeOnly("maven.modrinth:advanced-netherite:$loader-${deps("advanced_netherite")}-mc$minecraft")

    modstitchRuntimeOnly("org.antlr:antlr4-runtime:4.13.1")
    modstitchRuntimeOnly("io.github.douira:glsl-transformer:2.0.1")
    modstitchRuntimeOnly("org.anarres:jcpp:1.4.14")
}

modstitch {
    minecraftVersion = minecraft

    javaTarget = when (minecraft) {
        "1.21.5" -> 21
        else -> throw IllegalArgumentException("Unsupported Minecraft version: ${deps("minecraft")}")
    }

    parchment {
        deps("parchment") { mappingsVersion = it }
    }

    metadata {
        modId = "trimica"
        modName = "Trimica"
        modVersion = "1.0.0"
        modGroup = "com.bawnorton.trimica"
        modAuthor = "Bawnorton"
        modLicense = "MIT"
        modDescription = """
            A successor to AllTheTrims and DynamicTrims that allows you to trim any wearable item with anything 
            while also providing per-trim item texture overrides in a compatible way. Also includes some additional 
            items and features to make trimming more interesting.
         """.trimIndent()

        fun <K, V> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            put("mod_issue_tracker", "https://github.com/Bawnorton/Trimica/issues")
            put("minecraft_version", minecraft)
            put(
                "pack_format", when (deps("minecraft")) {
                    "1.21.5" -> 46
                    else -> throw IllegalArgumentException("Unsupported Minecraft version: ${deps("minecraft")}")
                }.toString()
            )
        }
    }

    val applyMixinDebugConfig: (Any, ConfigurationContainer) -> Unit = { runConfig, configurations ->
        val mixinJarFile = configurations.named("runtimeClasspath").get().incoming.artifactView {
            componentFilter {
                it is ModuleComponentIdentifier && it.group == "net.fabricmc" && it.module == "sponge-mixin"
            }
        }.files.singleFile
        val agentArg = "-javaagent:$mixinJarFile"
        val apply = fun (vmArg: (String) -> Unit, property: (String, String) -> Unit) {
            vmArg(agentArg)
            vmArg("-XX:+AllowEnhancedClassRefinition")
            property("mixin.hotSwap", "true")
            property("mixin.debug.export", "true")
        }
        when (runConfig) {
            is RunConfigSettings -> apply({ runConfig.vmArg(it) }, { k, v -> runConfig.property(k, v) })
            is RunModel -> apply({ runConfig.jvmArgument(it) }, { k, v -> runConfig.systemProperty(k, v) })
            else -> throw IllegalArgumentException("Unknown run config type: ${runConfig::class.java}")
        }
    }

    loom {
        fabricLoaderVersion = "0.16.10"

        configureLoom {
            (project.extensions.findByName("fabricApi") as? FabricApiExtension)?.configureDataGeneration {
                createRunConfiguration = true
                client = true
                modId = "trimica"
                outputDirectory = rootProject.file("src/main/generated")
            }

            accessWidenerPath.set(rootProject.file("src/main/resources/$minecraft.accesswidener"))

            runConfigs.all {
                ideConfigGenerated(true)
                runDir = "../../run"
            }

            runConfigs["client"].apply {
                programArgs("--username=Bawnorton", "--uuid=17c06cab-bf05-4ade-a8d6-ed14aaf70545")
                name = "Fabric Client"
            }

            runConfigs["server"].apply {
                name = "Fabric Server"
            }

            afterEvaluate {
                this@configureLoom.runs.configureEach {
                    applyMixinDebugConfig(this, configurations)
                }
            }
        }
    }

    moddevgradle {
        enable {
            deps("neoform") { neoFormVersion = it }
            deps("neoforge") { neoForgeVersion = it }
        }

        sourceSets.main {
            resources.srcDirs("src/main/generated", "src/main/resources")
        }

        defaultRuns()

        configureNeoforge {
            validateAccessTransformers = true

            runs.all {
                gameDirectory = file("../../run")
            }

            runs["client"].apply {
                programArgument("--username=Bawnorton")
                programArgument("--uuid=17c06cab-bf05-4ade-a8d6-ed14aaf70545")
            }

            afterEvaluate {
                this@configureNeoforge.runs.configureEach {
                    applyMixinDebugConfig(this, configurations)
                }
            }

            accessTransformers.from(rootProject.file("src/main/resources/$minecraft-accesstransformer.cfg"))
        }
    }

    mixin {
        addMixinsToModManifest = true

        configs.register("trimica")
        configs.register("trimica-client")
        if(modstitch.isLoom) {
            configs.register("trimica-fabric")
        }
    }
}

stonecutter {
    consts(
        "fabric" to (loader == "fabric"),
        "neoforge" to (loader == "neoforge")
    )
}

tasks {
    modstitch.finalJarTask {
        val modName = modstitch.metadata.modName.get()
        val modVersion = modstitch.metadata.modVersion.get()
        val modLoader = when {
            modstitch.isLoom -> "fabric"
            modstitch.isModDevGradle -> "neoforge"
            else -> throw IllegalStateException("Unknown platform: ${modstitch.platform}")
        }
        val modFileName = "$modName-$modLoader+$modVersion.jar"
        archiveBaseName.set(modName)
        archiveVersion.set(modVersion)
        archiveClassifier.set(modLoader)
        archiveFileName.set(modFileName)
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(modstitch.finalJarTask.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${modstitch.metadata.modVersion.get()}"))
        dependsOn(modstitch.finalJarTask)
    }

    processResources {
        outputs.upToDateWhen { false } // work around modstitch mixin cache issue
    }
}
