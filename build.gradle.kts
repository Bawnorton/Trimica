import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import net.fabricmc.loom.configuration.ide.RunConfigSettings
import net.neoforged.moddevgradle.dsl.RunModel

plugins {
    id("dev.isxander.modstitch.base") version "0.5.14-unstable"
    id("dev.kikugie.j52j") version "2.0"
}

fun deps(name: String): String? = findProperty("deps.${name}") as String?
fun deps(name: String, consumer: (prop: String) -> Unit) = deps(name)?.let(consumer)

val split = name.lastIndexOf('-')
val minecraft: String = name.substring(0, split)
var loader: String = name.substring(split + 1)

layout.buildDirectory = rootProject.file("build/versions/$minecraft/$loader")

repositories {
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://api.modrinth.com/maven")
    maven("https://maven.blamejared.com/")
    maven("https://maven.shedaniel.me/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

dependencies {
    modstitch.loom {
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${deps("fabric_api")}")
    }

    modstitch.moddevgradle {
    }

    deps("advanced-netherite") {
        modstitchModRuntimeOnly("maven.modrinth:advanced-netherite:$loader-$it-mc$minecraft")
    }
    deps("sodium") { it ->
        modstitchModImplementation("maven.modrinth:sodium:mc$minecraft-$it-$loader")
        deps("iris") {
            modstitchModRuntimeOnly("maven.modrinth:iris:$it+$minecraft-$loader")
            modstitchRuntimeOnly("org.antlr:antlr4-runtime:4.13.1")
            modstitchRuntimeOnly("io.github.douira:glsl-transformer:2.0.1")
            modstitchRuntimeOnly("org.anarres:jcpp:1.4.14")
        }
    }
    deps("jei") {
        modstitchModCompileOnly("mezz.jei:jei-$minecraft-$loader-api:$it")
        modstitchModImplementation("mezz.jei:jei-$minecraft-$loader:$it")
    }
    deps("rei") {
        modstitchModCompileOnly("me.shedaniel:RoughlyEnoughItems-api-$loader:$it")
        modstitchModCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-$loader:$it")
//        modstitchModRuntimeOnly("me.shedaniel:RoughlyEnoughItems-$loader:$it")
    }
    deps("elytra-trims") { it ->
        modstitchModImplementation("maven.modrinth:elytra-trims:$it")
        deps("fabric-language-kotlin") {
            modstitchModRuntimeOnly("net.fabricmc:fabric-language-kotlin:$it")
        }
        deps("kotlinforforge-neoforge") {
            modstitchModRuntimeOnly("thedarkcolour:kotlinforforge-neoforge:$it")
        }
    }
}

modstitch {
    minecraftVersion = minecraft

    javaTarget = when (minecraft) {
        "1.21.5", "1.21.6" -> 21
        else -> throw IllegalArgumentException("Unsupported Minecraft version: $minecraft")
    }

    parchment {
        minecraftVersion = "1.21.5"
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
         """.trimIndent().replace("\n", " ")

        fun <K, V> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            put("mod_issue_tracker", "https://github.com/Bawnorton/Trimica/issues")
            put("minecraft_version", minecraft)
            put(
                "pack_format", when (minecraft) {
                    "1.21.5" -> 71
                    "1.21.6" -> 77
                    else -> throw IllegalArgumentException("Unsupported Minecraft version: $minecraft")
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
        fabricLoaderVersion = "0.16.14"

        configureLoom {
            fun fapi(action: FabricApiExtension.() -> Unit) = (project.extensions.findByName("fabricApi") as FabricApiExtension).action()

            fapi {
                configureDataGeneration {
                    createRunConfiguration = true
                    client = true
                    modId = "trimica"
                    outputDirectory = rootProject.file("src/main/generated")
                }

                @Suppress("UnstableApiUsage")
                configureTests {
                    enableGameTests = false
                    eula = true
                    clearRunDirectory = false
                }
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

        defaultRuns()

        configureNeoforge {
            validateAccessTransformers = true

            runs.all {
                gameDirectory = file(rootProject.file("run"))
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

sourceSets.main {
    modstitch {
        moddevgradle {
            resources.srcDir(rootProject.file("src/main/generated")) // add fabric datagen output to neo
            resources.exclude(".cache")
        }
    }
}

stonecutter {
    consts(
        "fabric" to (loader == "fabric"),
        "neoforge" to (loader == "neoforge"),
        "rei" to (deps("rei") != null),
        "jei" to (deps("jei") != null)
    )
}

j52j {
    params {
        prettyPrinting = true
    }
}

tasks {
    modstitch.finalJarTask {
        val modName = modstitch.metadata.modName.get()
        val modVersion = modstitch.metadata.modVersion.get()
        archiveBaseName.set(modName)
        archiveVersion.set("$modVersion+$minecraft")
        archiveClassifier.set(loader)
    }

    processResources {
        // work around modstitch mixin cache issue
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        outputs.upToDateWhen { false }

        val refmap = "refmap" to "${modstitch.metadata.modId.get()}.refmap.json"
        inputs.properties(refmap)

        filesMatching("trimica-fabric.mixins.json5") {
            expand(refmap)
        }

        exclude {
            val name = it.name
            val awExclude = it.name.endsWith(".accesswidener") && name != "$minecraft.accesswidener"
            val atExclude = it.name.endsWith("-accesstransformer.cfg") && name != "$minecraft-accesstransformer.cfg"
            awExclude || atExclude
        }
    }

    generateModMetadata {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }

    clean {
        delete(rootProject.layout.buildDirectory)
        delete(project.file("build"))
    }
}
