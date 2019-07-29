package de.randombyte.lighthub.osc.devices.features.impl

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.AdjScannerColorWheelFeature

class AdjScannerColorWheelFeatureImpl(val oscColorWheel: OscChannel) : AdjScannerColorWheelFeature {

    override var color: AdjScannerColorWheelFeature.Color =
        AdjScannerColorWheelFeature.Color.White
        set(color) {
            field = color
            sendOsc()
        }

    private fun sendOsc() {
        oscColorWheel.sendValue(color.dmxValue)
    }
}