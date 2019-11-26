package de.randombyte.lighthub.show.flows.intensity

import de.randombyte.lighthub.config.ConfigDescription
import de.randombyte.lighthub.show.flows.AutoPatternsConfig

class IntensityAutoPatternsConfig(
    interval: Int = 8,
    `device-type-offset`: Int = 0,
    `global-type-offset`: Int = 0
) : AutoPatternsConfig(interval, `device-type-offset`, `global-type-offset`) {
    companion object : ConfigDescription {
        override val fileName = "intensity"
    }
}