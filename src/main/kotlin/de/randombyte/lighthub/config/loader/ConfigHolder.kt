package de.randombyte.lighthub.config.loader

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import de.randombyte.lighthub.Constants
import io.github.config4k.ClassContainer
import io.github.config4k.TypeReference
import io.github.config4k.readers.SelectReader
import io.github.config4k.toConfig
import java.io.File
import java.nio.file.Files
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Caches a config file.
 */
open class ConfigHolder<T : Any>(val clazz: KClass<T>, val name: String, val file: File) {

    companion object {
        val configRenderOptions = ConfigRenderOptions.defaults()
            .setJson(false)
            .setOriginComments(false)
    }

    private val genericType = object : TypeReference<T>() {}.genericType()

    lateinit var config: T

    fun reload() {
        val config = ConfigFactory.parseFile(file)
        val result = SelectReader.getReader(ClassContainer(clazz, genericType))(config, name) ?: clazz.createInstance()

        try {
            this.config = result as T
        } catch (exception: Exception) {
            throw exception
        }

        save() // generates missing values, corrects identation, etc.
    }

    fun save() {
        file.writeText(config.toConfig(name).root().render(configRenderOptions))
    }
}

inline fun <reified T : Any> String.toConfigHolder(): ConfigHolder<T> {
    if (Files.notExists(Constants.CONFIG_PATH)) Files.createDirectories(Constants.CONFIG_PATH)
    return ConfigHolder(
        clazz = T::class,
        name = this.substringBefore("."),
        file = Constants.CONFIG_PATH.resolve(this).toFile()
    )
}