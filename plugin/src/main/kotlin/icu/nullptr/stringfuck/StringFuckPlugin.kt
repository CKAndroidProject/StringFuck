package icu.nullptr.stringfuck

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import icu.nullptr.stringfuck.code.FuckClassGenerator
import icu.nullptr.stringfuck.util.Encryptors
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

@Suppress("unused", "UnstableApiUsage")
class StringFuckPlugin : Plugin<Project> {

    companion object {
        const val PLUGIN_NAME = "stringFuck"
    }

    override fun apply(project: Project) {
        project.configurations.create(PLUGIN_NAME)
        StringFuckOptions.INSTANCE = project.extensions.create(PLUGIN_NAME, StringFuckOptions::class.java, project)
        val options = StringFuckOptions.INSTANCE

        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
        androidComponents?.onVariants { variant ->
            if (options.isWorkOnDebug || variant.buildType?.contains("debug") == false) {
                variant.transformClassesWith(StringFuckClassVisitorFactory::class.java, InstrumentationScope.ALL) {}
                variant.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
            }
        }

        // TODO: Old API, but no replacement yet
        val android = project.extensions.findByType(AppExtension::class.java)
        project.afterEvaluate {
            if (options.encryptMethod == null) options.encryptMethod = Encryptors.xor
            if (options.decryptMethodClassPath == null) options.decryptMethodClassPath = "icu.nullptr.stringfuck.Xor"
            android?.applicationVariants?.forEach { variant ->
                if (options.isWorkOnDebug || !variant.buildType.isDebuggable) {
                    generateSources(project, variant)
                }
            }
        }
    }

    private fun generateSources(project: Project, variant: ApplicationVariant) {
        val variantCapped = variant.name.capitalize(Locale.ROOT)
        val sourceDir = project.buildDir.resolve("generated/source/stringFuck/icu/nullptr")
        val generateTask = project.tasks.create("generate${variantCapped}StringFuckSources").doLast {
            sourceDir.mkdirs()
            FuckClassGenerator.generate(sourceDir)
        }

        variant.registerJavaGeneratingTask(generateTask, sourceDir)
    }
}
