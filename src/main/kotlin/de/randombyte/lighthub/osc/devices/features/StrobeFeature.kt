package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.config.createConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.StrobeFeature.*
import de.randombyte.lighthub.osc.devices.features.StrobeFeature.Companion.STROBE_SPEED_RANGE
import de.randombyte.lighthub.utils.length

interface StrobeFeature : Feature {
    companion object {
        val STROBE_SPEED_RANGE = 0.0..1.0
    }

    val strobeActivated: Boolean

    /**
     * Only returns meaningful values if [strobeActivated] is true.
     * Setting a strobe speed activates the strobe immediately.
     */
    var strobeSpeed: Double

    fun slowStrobe()
    fun fastStrobe()

    val strobeSpeeds: ConfigHolder<StrobeSpeedConfig>

    class StrobeSpeedConfig(val slow: Double = 0.5, val fast: Double = 0.9)
}

interface StrobeFeatureImpl : StrobeFeature {
    val oscSpeed: OscChannel

    val oscSpeedRange: IntRange

    override val strobeSpeeds get() = createConfigHolder<StrobeSpeedConfig>("strobe-speeds")

    override val strobeActivated: Boolean
        get() = oscSpeed.lastValue in oscSpeedRange

    override var strobeSpeed: Double
        get() {
            return (oscSpeed.lastValue - oscSpeedRange.first).toDouble() / oscSpeedRange.length
        }
        set(value) {
            val dmxValue = (value.coerceIn(STROBE_SPEED_RANGE) * oscSpeedRange.length) + oscSpeedRange.first
            oscSpeed.sendValue(dmxValue.toInt())
        }

    override fun slowStrobe() {
        strobeSpeed = strobeSpeeds.config.slow
    }

    override fun fastStrobe() {
        strobeSpeed = strobeSpeeds.config.fast
    }
}