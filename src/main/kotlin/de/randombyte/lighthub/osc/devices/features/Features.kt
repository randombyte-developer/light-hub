package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder

interface Feature {
    interface Configurable : Feature {
        val configHolders: List<ConfigHolder<*>>
    }
}