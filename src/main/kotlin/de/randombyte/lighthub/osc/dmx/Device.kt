package de.randombyte.lighthub.osc.dmx

import de.randombyte.lighthub.config.loader.ConfigHolder
import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.osc.Receiver

abstract class Device(val type: Type, oscBasePath: String, val number: Int, val startAddress: Int) : Receiver("$oscBasePath/$number") {

    companion object {
        val types = listOf(AdjPar, LedBar, TsssPar)
    }

    val channels = 0 until type.channels
    abstract val oscChannelMapping: OscChannelMapping

    open class Config(
        val meta: Meta,
        val addresses: List<Int>
    ) {
        class Meta(
            val manufacturer: String = "",
            val model: String = "",
            val mode: String = "",
            val name: String = "",
            val `short-name`: String = ""
        )
    }

    // Common values
    interface Type {
        val configHolder: ConfigHolder<out Config>
        val channels: Int
    }
}