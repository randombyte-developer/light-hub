package de.randombyte.lighthub.osc

import de.randombyte.lighthub.utils.CRC16
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

open class OscChannel(val path: String) {

    val qlcChannel = CRC16.checksum(path.toCharArray())

    var lastValue: Int = 0
        protected set

    open fun sendValue(value: Int): Int {
        val coercedValue = value.coerceIn(DMX_RANGE)
        Osc.send(path, coercedValue)
        lastValue = coercedValue

        return coercedValue
    }

    /**
     * Bundles many [channels] to one [OscChannel].
     */
    class OscMultiChannel(vararg val channels: OscChannel) : OscChannel("") {
        override fun sendValue(value: Int): Int {
            val coercedValue = value.coerceIn(DMX_RANGE)
            channels.forEach { Osc.send(it.path, coercedValue) }
            lastValue = coercedValue

            return coercedValue
        }
    }
}