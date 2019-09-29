package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.show.tickables.AutoPatternsConfig
import de.randombyte.lighthub.utils.Ranges
import de.randombyte.lighthub.utils.coerceIn

interface RotationFeature : Feature {
    /**
     * Value must be in [Ranges.DMX_RANGE]
     */
    var rotationSpeed: Int

    val rotationSpeeds get() = (type as Config).rotationSpeeds.config
    val rotationAutoPatterns get() = (type as Config).rotationAutoPatterns.config
    interface Config {
        val rotationSpeeds: ConfigHolder<RotationSpeedsConfig>
        val rotationAutoPatterns: ConfigHolder<RotationAutoPatternsConfig>
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

    class RotationAutoPatternsConfig(
        val changeBeatPhase: Int = 4,
        val changeBeatOffset: Int = 0
    ) : AutoPatternsConfig() {
        companion object {
            const val FILE_NAME = "rotation-auto-patterns"
        }
    }
}

interface RotationFeatureImpl: RotationFeature {
    val oscRotation: OscChannel

    override var rotationSpeed: Int
        get() = oscRotation.lastValue
        set(value) {
            oscRotation.sendValue(value.coerceIn(Ranges.DMX_RANGE, "Rotation speed"))
        }
}