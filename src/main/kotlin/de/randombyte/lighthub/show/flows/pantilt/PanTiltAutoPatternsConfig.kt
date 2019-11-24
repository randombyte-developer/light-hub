package de.randombyte.lighthub.show.flows.pantilt

import de.randombyte.lighthub.show.flows.AutoPatternsConfig

open class PanTiltAutoPatternsConfig(
    `change-every-n-beats`: Int = 4,
    `change-beats-offset`: Int = 0
) : AutoPatternsConfig(`change-every-n-beats`, `change-beats-offset`) {
    companion object {
        const val FILE_NAME = "pan-tilt-auto-pattern"
    }
}