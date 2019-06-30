package de.randombyte.lighthub.dmx

import de.randombyte.lighthub.config.loader.ConfigHolder

abstract class Device (val oscBasePath: String, val startAddress: Int) {
    open class Config(
        val meta: Meta,
        val addresses: List<UByte>
    ) {
        class Meta(
            val manufacturer: String = "",
            val model: String = "",
            val mode: String = "",
            val name: String = ""
        )
    }

    // Common values
    interface Type {
        val configHolder: ConfigHolder<out Config>
        val channels: Int
    }
}