package de.randombyte.lighthub.show.flows.pantilt

import de.randombyte.lighthub.config.ConfigDescription
import de.randombyte.lighthub.show.flows.AutoPatternsConfig

open class PanTiltAutoPatternsConfig(
    interval: Int = 16,
    `device-type-offset`: Int = 0,
    `global-type-offset`: Int = 0
) : AutoPatternsConfig(interval, `device-type-offset`, `global-type-offset`) {
    companion object : ConfigDescription {
        override val fileName = "pan-tilt-auto-pattern"
    }
}