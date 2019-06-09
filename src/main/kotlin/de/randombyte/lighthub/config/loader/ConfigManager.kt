package de.randombyte.lighthub.config.loader

import com.google.common.reflect.TypeToken
import de.randombyte.lighthub.Constants
import de.randombyte.lighthub.config.Color
import de.randombyte.lighthub.config.serializer.UByteTypeSerializer
import de.randombyte.lighthub.typeToken
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import java.nio.file.Files
import java.nio.file.Paths

class ConfigManager <T : Any> (val configLoader: ConfigurationLoader<CommentedConfigurationNode>, clazz: Class<T>) {

    companion object {
        private val options: ConfigurationOptions = ConfigurationOptions.defaults()
            .setSerializers(
                TypeSerializers.newCollection()
                    .registerType(UByte::class.typeToken, UByteTypeSerializer()))
                    //.registerPredicate({typeToken: TypeToken<Any> -> typeToken.isSupertypeOf(Color::class.java) }, ))
    }
    private val typeToken: TypeToken<T> = clazz.kotlin.typeToken

    @Suppress("UNCHECKED_CAST")
    fun load(): T = configLoader.load(options).getValue(typeToken) ?: {
        save(typeToken.rawType.newInstance() as T)
        load()
    }.invoke()

    fun save(config: T) = configLoader.apply { save(load(options).setValue(typeToken, config)) }

    fun generate() = save(load())
}

fun String.toConfigLoader(): HoconConfigurationLoader {
    if (Files.notExists(Constants.CONFIG_PATH)) Files.createDirectories(Constants.CONFIG_PATH)
    return HoconConfigurationLoader.builder().setPath(Constants.CONFIG_PATH.resolve(this)).build()
}