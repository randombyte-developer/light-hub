package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.osc.Device

/**
 * Abilities of devices like setting RGB color or strobing.
 */
interface Feature {
    /**
     * The [Device.Type] which implements this feature.
     */
    val type: Device.Type<*>
}