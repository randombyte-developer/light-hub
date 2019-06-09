package de.randombyte.lighthub.dmx

import de.randombyte.lighthub.config.loader.ConfigHolder
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

abstract class Device (val oscBasePath: String, val startAddress: Int) {
    @ConfigSerializable open class Config(
        @Setting val meta: Meta,
        @Setting val addresses: List<UByte>
    ) {
        @ConfigSerializable class Meta(
            @Setting val manufacturer: String = "",
            @Setting val model: String = "",
            @Setting val mode: String = "",
            @Setting val name: String = ""
        )
    }

    // Common values
    interface Type {
        val config: ConfigHolder<out Config>
        val channels: Int
    }
}