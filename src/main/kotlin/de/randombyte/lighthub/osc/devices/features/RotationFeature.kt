package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.utils.Ranges
import de.randombyte.lighthub.utils.coerceIn
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface RotationFeature : Feature {
    /**
     * Value must be in [Ranges.DMX_RANGE]
     */
    var rotationSpeed: Int

    val rotationSpeeds get() = (type as Config).rotationSpeeds.config
    interface Config {
        val rotationSpeeds: ConfigHolder<RotationSpeedsConfig>
    }

    class RotationSpeedsConfig(
        val none: Int = -1,
        val slow: List<Int> = emptyList(),
        val normal: List<Int> = emptyList(),
        val fast: List<Int> = emptyList()
    ) {
        companion object {
            const val FILE_NAME = "rotation-speeds"
        }
    }
}

@ExperimentalTime
interface RotationFeatureImpl: RotationFeature {
    val oscRotation: OscChannel

    override var rotationSpeed: Int
        get() = oscRotation.lastValue
        set(value) {
            oscRotation.sendValue(value.coerceIn(Ranges.DMX_RANGE, "Rotation speed"))
        }
}