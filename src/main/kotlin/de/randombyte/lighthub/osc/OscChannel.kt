package de.randombyte.lighthub.osc

import de.randombyte.lighthub.qlc.Osc
import de.randombyte.lighthub.utils.CRC16

class OscChannel(val path: String) {

    val qlcChannel = CRC16.checksum(path.toCharArray())

    var lastValue: Int = 0
        private set

    fun sendValue(value: Int) {
        Osc.send(path, value)
        lastValue = value
    }
}