package de.randombyte.lighthub.config

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

        inline fun <reified T : Any> create(folder: String, name: String): ConfigHolder<T> {
            val configPath = Constants.CONFIG_PATH.resolve(folder)
            if (Files.notExists(configPath)) Files.createDirectories(configPath)

            return ConfigHolder(T::class, name, configPath.resolve("$name.conf").toFile())
        }
    }

    lateinit var config: T

    private val genericType = object : TypeReference<T>() {}

    fun reload() {
        val config = ConfigFactory.parseFile(file)
        val result = SelectReader.getReader(ClassContainer(clazz, genericType.genericType()))(config, name) ?: clazz.createInstance()

        this.config = result as T

        save() // generates missing values, corrects indentation, etc.
    }

    fun save() {
        file.writeText(config.toConfig(name).root().render(configRenderOptions))
    }
}