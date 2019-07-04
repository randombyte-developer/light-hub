package de.randombyte.lighthub.osc.dmx

import de.randombyte.lighthub.Animation
import de.randombyte.lighthub.config.Color
import de.randombyte.lighthub.config.loader.ConfigHolder

abstract class Light<C : Color>(type: Device.Type, oscBasePath: String, number: Int, startAddress: Int) : Device(type, oscBasePath, number, startAddress) {

    open class Config<C : Color>(
        meta: Meta,
        addresses: List<UByte>,
        val colors: Map<String, C> // must always have a key [Colors.DEFAULT_COLOR_KEY], for default values
    ) : Device.Config(meta, addresses)

    // Common values
    interface Type<C : Color> : Device.Type {
        override val configHolder: ConfigHolder<out Config<C>>
    }

    abstract var color: C

    // null -> static scene
    val animation: Animation? = null

    abstract fun blackout()
}