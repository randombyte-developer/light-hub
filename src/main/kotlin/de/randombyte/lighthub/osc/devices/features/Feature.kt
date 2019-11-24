package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.osc.Device
import kotlin.time.ExperimentalTime

/**
 * Abilities of devices like setting RGB color or strobing.
 */
@ExperimentalTime
interface Feature {
    /**
     * The [Device.Type] which implements this feature.
     */
    val type: Device.Type<*>
}