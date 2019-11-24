package de.randombyte.lighthub.config

import java.nio.file.Path

object ConfigHoldersCache {

    val configHolders = mutableMapOf<Path, ConfigHolder<*>>()

    inline fun <reified T : Any> getOrLoadConfigHolder(folder: Path, name: String): ConfigHolder<T> {
        val fullPath = folder.resolve("$name.conf")
        if (fullPath in configHolders.keys) {
            return configHolders.getValue(fullPath) as ConfigHolder<T>
        }

        val configHolder = createConfigHolder<T>(folder, name)
        configHolders[fullPath] = configHolder

        return configHolder
    }
}