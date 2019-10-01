package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.utils.Ranges

interface ShutterFeature {
    fun fullIntensity()
    fun noLight()
}

interface ShutterFeatureImpl : ShutterFeature {
    val oscShutter: OscChannel

    val oscFullIntensity: Int get() = Ranges.DMX_RANGE.last
    val oscNoLight: Int get() = Ranges.DMX_RANGE.first

    override fun fullIntensity() {
        oscShutter.sendValue(oscFullIntensity)
    }

    override fun noLight() {
        oscShutter.sendValue(oscNoLight)
    }
}