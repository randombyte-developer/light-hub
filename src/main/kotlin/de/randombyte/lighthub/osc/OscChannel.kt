package de.randombyte.lighthub.osc

import de.randombyte.lighthub.utils.CRC16
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

class OscChannel(val path: String) {

    val qlcChannel = CRC16.checksum(path.toCharArray())

    var lastValue: Int = 0
        private set

    fun sendValue(value: Int): Int {
        val coercedValue = value.coerceIn(DMX_RANGE)
        Osc.send(path, coercedValue)
        lastValue = coercedValue

        return coercedValue
    }
}