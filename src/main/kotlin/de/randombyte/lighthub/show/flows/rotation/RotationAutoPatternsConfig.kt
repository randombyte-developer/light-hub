package de.randombyte.lighthub.show.flows.rotation

import de.randombyte.lighthub.show.flows.AutoPatternsConfig

class RotationAutoPatternsConfig(
    `change-every-n-beats`: Int = 4,
    `change-beats-offset`: Int = 0
) : AutoPatternsConfig(`change-every-n-beats`, `change-beats-offset`) {
    companion object {
        const val FILE_NAME = "rotation-auto-patterns"
    }
}