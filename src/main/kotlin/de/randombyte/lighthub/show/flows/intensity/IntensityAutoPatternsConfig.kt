package de.randombyte.lighthub.show.flows.intensity

import de.randombyte.lighthub.config.ConfigDescription
import de.randombyte.lighthub.show.flows.AutoPatternsConfig

class IntensityAutoPatternsConfig(
    interval: Int,
    `device-type-offset`: Int,
    `global-type-offset`: Int
) : AutoPatternsConfig(interval, `device-type-offset`, `global-type-offset`) {
    companion object : ConfigDescription {
        override val fileName = "intensity"
    }
}