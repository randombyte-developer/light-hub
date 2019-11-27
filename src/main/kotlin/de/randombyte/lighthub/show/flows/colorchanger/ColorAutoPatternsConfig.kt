package de.randombyte.lighthub.show.flows.colorchanger

import de.randombyte.lighthub.config.ConfigDescription
import de.randombyte.lighthub.show.flows.AutoPatternsConfig

open class ColorAutoPatternsConfig(
    interval: Int = 4,
    `device-type-offset`: Int = 0,
    `global-type-offset`: Int = 0
) : AutoPatternsConfig(interval, `device-type-offset`, `global-type-offset`) {
    companion object : ConfigDescription {
        override val fileName = "color-auto-pattern"
    }
}