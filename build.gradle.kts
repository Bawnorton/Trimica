plugins {
    id("dev.isxander.modstitch.base") version "0.5.12"
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)?.let(consumer)
}

val minecraft = property("deps.minecraft") as String

dependencies {
    modstitch.loom {
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:0.122.0+1.21.5")
    }
}

modstitch {
    minecraftVersion = minecraft

    javaTarget = when (minecraft) {
        "1.21.5" -> 21
        else -> throw IllegalArgumentException("Unsupported Minecraft version: ${property("deps.minecraft")}")
    }

    parchment {
        prop("deps.parchment") { mappingsVersion = it }
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
            put(
                "pack_format", when (property("deps.minecraft")) {
                    "1.21.5" -> 46
                    else -> throw IllegalArgumentException("Unsupported Minecraft version: ${property("deps.minecraft")}")
                }.toString()
            )
        }
    }

    loom {
        fabricLoaderVersion = "0.16.10"

        configureLoom {
        }
    }

    moddevgradle {
        enable {
            prop("deps.neoform") { neoFormVersion = it }
            prop("deps.neoforge") { neoForgeVersion = it }
        }

        defaultRuns()

        configureNeoforge {
            runs.all {
                disableIdeRun()
            }
        }
    }

    mixin {
        addMixinsToModManifest = true

        configs.register("trimica")
    }
}


var constraint: String = name.split("-")[1]
stonecutter {
    consts(
        "fabric" to (constraint == "fabric"),
        "neoforge" to (constraint == "neoforge")
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
}
