package de.randombyte.lighthub.dmx

import de.randombyte.lighthub.Animation
import de.randombyte.lighthub.config.Color
import de.randombyte.lighthub.config.loader.ConfigHolder
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

abstract class Light<C : Color> (oscBasePath: String, startAddress: Int) : Device(oscBasePath, startAddress) {

    @ConfigSerializable open class Config<C : Color>(
        meta: Meta,
        addresses: List<UByte>,
        @Setting val colors: Map<String, C> // must always have a key [Colors.DEFAULT_COLOR_KEY], for default values
    ) : Device.Config(meta, addresses)

    // Common values
    interface Type<C : Color> : Device.Type {
        override val config: ConfigHolder<out Config<C>>
        val colors: Map<String, C>
    }

    // null -> static scene
    val animation: Animation? = null

    abstract fun blackout()
}