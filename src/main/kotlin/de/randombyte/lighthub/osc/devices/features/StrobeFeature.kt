package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.StrobeFeature.Companion.STROBE_SPEED_RANGE
import de.randombyte.lighthub.utils.coerceIn
import de.randombyte.lighthub.utils.length
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface StrobeFeature : Feature {
    companion object {
        val STROBE_SPEED_RANGE = 0.0..1.0
    }

    val strobeActivated: Boolean

    /**
     * Only accepts values in range of [STROBE_SPEED_RANGE].
     * Only returns meaningful values if [strobeActivated] is true.
     * Setting a strobe speed activates the strobe immediately.
     */
    var strobeSpeed: Double

    fun slowStrobe()
    fun fastStrobe()
    fun noStrobe()

    val strobeSpeeds get() = (type as Config).strobeSpeeds.config
    interface Config {
        val strobeSpeeds: ConfigHolder<StrobeSpeedsConfig>
    }

    class StrobeSpeedsConfig(val slow: Double = 0.5, val fast: Double = 0.9) {
        companion object {
            const val FILE_NAME = "strobe-speeds"
        }
    }
}

@ExperimentalTime
interface StrobeFeatureImpl : StrobeFeature {
    val oscStrobeSpeed: OscChannel

    val oscSpeedRange: IntRange
    val oscNoStrobe: Int

    override val strobeActivated: Boolean
        get() = oscStrobeSpeed.lastValue in oscSpeedRange

    override var strobeSpeed: Double
        get() {
            return (oscStrobeSpeed.lastValue - oscSpeedRange.first).toDouble() / oscSpeedRange.length
        }
        set(value) {
            val dmxValue = (value.coerceIn(STROBE_SPEED_RANGE, "Strobe speed") * oscSpeedRange.length) + oscSpeedRange.first
            oscStrobeSpeed.sendValue(dmxValue.toInt())
        }

    override fun slowStrobe() {
        strobeSpeed = strobeSpeeds.slow
    }

    override fun fastStrobe() {
        strobeSpeed = strobeSpeeds.fast
    }

    override fun noStrobe() {
        oscStrobeSpeed.sendValue(oscNoStrobe)
    }
}