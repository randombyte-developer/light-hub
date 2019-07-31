package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.StrobeFeature.Companion.STROBE_SPEED_RANGE
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE
import de.randombyte.lighthub.utils.length

interface StrobeFeature : Feature {
    companion object {
        val STROBE_SPEED_RANGE = 0.0..1.0
    }

    fun strobeOn()
    fun strobeOff()

    var strobeSpeed: Double
}

interface StrobeFeatureImpl : StrobeFeature {
    val oscSpeed: OscChannel

    val oscSpeedRange: IntRange

    override var strobeSpeed: Double
        get() = (oscSpeed.lastValue - oscSpeedRange.first).toDouble() / oscSpeedRange.length
        set(value) {
            val dmxValue = (value.coerceIn(STROBE_SPEED_RANGE) * oscSpeedRange.length) + oscSpeedRange.first
            oscSpeed.sendValue(dmxValue.toInt())
        }
}