package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.PanTiltFeature.Position
import de.randombyte.lighthub.show.tickables.AutoPatternsConfig

interface PanTiltFeature : Feature {
    class Position(val pan: Int, val tilt: Int)

    var position: Position

    open class PanTiltAutoPatternsConfig(
        `change-every-n-beats`: Int = 4,
        `change-beats-offset`: Int = 0,
        val `pan-min`: Int = 50,
        val `pan-max`: Int = 200,
        val `tilt-min`: Int = 50,
        val `tilt-max`: Int = 200
    ) : AutoPatternsConfig(`change-every-n-beats`, `change-beats-offset`) {
        companion object {
            const val FILE_NAME = "pan-tilt-auto-pattern"
        }
    }

    val panTiltAutoPatterns get() = (type as Config).panTiltAutoPatterns.config
    interface Config {
        val panTiltAutoPatterns: ConfigHolder<out PanTiltAutoPatternsConfig>
    }
}

interface PanTiltFeatureImpl : PanTiltFeature {
    val oscPan: OscChannel
    val oscTilt: OscChannel

    override var position: Position
        get() = Position(pan = oscPan.lastValue, tilt = oscTilt.lastValue)
        set(value) {
            oscPan.sendValue(value.pan)
            oscTilt.sendValue(value.tilt)
        }
}