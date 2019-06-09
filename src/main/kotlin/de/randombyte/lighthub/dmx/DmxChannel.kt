package de.randombyte.lighthub.dmx

import de.randombyte.lighthub.qlc.OscHandler

class DmxChannel(val oscPath: String) {

    var lastValue: UByte = 0u
        private set

    fun sendValue(value: UByte) {
        OscHandler.send(this, value)
        lastValue = value
    }
}