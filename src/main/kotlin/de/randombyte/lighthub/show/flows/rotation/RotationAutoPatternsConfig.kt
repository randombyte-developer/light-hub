package de.randombyte.lighthub.show.flows.rotation

import de.randombyte.lighthub.config.ConfigDescription
import de.randombyte.lighthub.show.flows.AutoPatternsConfig

class RotationAutoPatternsConfig(
    interval: Int = 8,
    `device-type-offset`: Int = 0,
    `global-type-offset`: Int = 0
) : AutoPatternsConfig(interval, `device-type-offset`, `global-type-offset`) {
    companion object : ConfigDescription {
        override val fileName = "rotation-auto-patterns"
    }
}