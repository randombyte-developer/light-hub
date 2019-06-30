package de.randombyte.lighthub.dmx

import de.randombyte.lighthub.qlc.OscHandler

class DmxChannel(val oscPath: String) {

    var lastValue: Int = 0
        private set

    fun sendValue(value: Int) {
        OscHandler.send(this, value)
        lastValue = value
    }
}