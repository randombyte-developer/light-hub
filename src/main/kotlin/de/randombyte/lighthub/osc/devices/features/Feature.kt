package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder

interface Feature

interface ConfigurableFeature : Feature {
    val configs: List<ConfigHolder<*>>

    fun reloadConfig() = configs.forEach { it.reload() }
}