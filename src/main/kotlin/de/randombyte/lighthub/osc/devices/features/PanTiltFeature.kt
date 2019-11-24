package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.PanTiltFeature.Position
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface PanTiltFeature : Feature {
    class Position(val pan: Int, val tilt: Int)

    var position: Position

    open class PanTiltBoundsConfig(
        val `pan-min`: Int = 50,
        val `pan-max`: Int = 200,
        val `tilt-min`: Int = 50,
        val `tilt-max`: Int = 200
    ) {
        companion object {
            const val FILE_NAME = "pan-tilt-bounds"
        }
    }

    val panTiltBounds get() = (type as Config).panTiltBounds.config
    interface Config {
        val panTiltBounds: ConfigHolder<out PanTiltBoundsConfig>
    }
}

@ExperimentalTime
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