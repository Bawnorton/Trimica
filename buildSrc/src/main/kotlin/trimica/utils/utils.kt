package trimica.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import java.util.function.BiConsumer
import java.util.function.Consumer

fun Project.deps(name: String): String? = findProperty("deps.${name}") as String?
fun Project.deps(name: String, consumer: (prop: String) -> Unit) = deps(name)?.let(consumer)

fun Project.mod(name: String): String? = findProperty("mod.${name}") as String?
fun Project.mod(name: String, consumer: (prop: String) -> Unit) = mod(name)?.let(consumer)

fun Project.applyMixinDebugSettings(vmArgConsumer: Consumer<String>, propertyConsumer: BiConsumer<String, String>) {
    val mixinJarFile = configurations.named("runtimeClasspath").get().incoming.artifactView {
        componentFilter {
            it is ModuleComponentIdentifier && it.group == "net.fabricmc" && it.module == "sponge-mixin"
        }
    }.files.singleFile
    vmArgConsumer.accept("-javaagent:$mixinJarFile")
    vmArgConsumer.accept("-XX:+AllowEnhancedClassRefinition")
    propertyConsumer.accept("mixin.hotSwap", "true")
    propertyConsumer.accept("mixin.debug.export", "true")
}

fun Project.remoteDepBuilder(project: Project, depResolver: (String, String, String) -> Dependency) : RemoteDepBuilder {
    return RemoteDepBuilder(project, depResolver)
}

class RemoteDepBuilder(private val project: Project, private val depResolver: (String, String, String) -> Dependency) {
    private val minecraft: String by lazy {
        project.extensions.extraProperties.get("minecraft") as String
    }

    private val loader: String by lazy {
        project.extensions.extraProperties.get("loader") as String
    }

    fun dep(id: String, version: String = minecraft, loader: String = this.loader, handler: (dep: Dependency) -> Unit) : RemoteDepBuilder {
        var dep: Dependency? = null
        try {
            dep = depResolver(id, version, loader)
        } catch (e: Exception) {
            project.logger.warn("Could not find remote dependency '$id' for Minecraft $version. ", e)
        }
        dep?.let { handler(it) }
        return this
    }
}