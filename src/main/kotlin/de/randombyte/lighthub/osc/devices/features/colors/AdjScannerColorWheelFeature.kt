package de.randombyte.lighthub.osc.devices.features.colors

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.Feature

/**
 * This feature is for the specific American DJ Scan 250 EX.
 */
class AdjScannerColorWheelFeature(val oscColorWheel: OscChannel) : Feature {

    var color: Color = Color.White
        set(color) {
            field = color
            sendOsc()
        }

    private fun sendOsc() {
        oscColorWheel.sendValue(color.dmxValue)
    }

    enum class Color(val dmxValue: Int) {
        White(0),
        Red(11),
        DeepBlue(22),
    }
}