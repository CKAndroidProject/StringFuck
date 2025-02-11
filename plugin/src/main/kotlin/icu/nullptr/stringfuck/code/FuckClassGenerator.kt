package icu.nullptr.stringfuck.code

import com.squareup.javawriter.JavaWriter
import icu.nullptr.stringfuck.StringFuckOptions
import java.io.File
import java.io.FileWriter
import javax.lang.model.element.Modifier

internal object FuckClassGenerator {

    fun generate(sourceDir: File) {
        val configFile = sourceDir.resolve("Config.java")
        JavaWriter(FileWriter(configFile)).use {
            it.indent = "    "
            it.emitPackage("icu.nullptr.stringfuck")

            it.beginType("Config", "class", setOf(Modifier.PUBLIC, Modifier.FINAL))
            it.emitEmptyLine()

            it.emitField(
                "Class<?>", "decryptorClass", setOf(Modifier.PUBLIC, Modifier.STATIC),
                "${StringFuckOptions.INSTANCE.decryptMethodClassPath}.class"
            )
            it.emitField(
                "byte[]", "key", setOf(Modifier.PUBLIC, Modifier.STATIC),
                "{${StringFuckOptions.INSTANCE.key.joinToString(",")}}"
            )

            it.beginInitializer(true)
            it.emitStatement("${StringFuckOptions.INSTANCE.decryptMethodClassPath}.decrypt(new byte[0])")
            it.emitStatement("${StringFuckOptions.INSTANCE.decryptMethodClassPath}.decrypt(new byte[0])")
            it.endInitializer()

            it.endType()
        }
    }
}
