package de.randombyte.lighthub.show.flows.manualcolor

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.features.RgbFeature
import de.randombyte.lighthub.osc.devices.features.RgbwFeature
import de.randombyte.lighthub.osc.devices.features.RgbwauvFeature

/**
 * Set the color of each device separately.
 */
class ManualColorFlow(val devices: List<Device>) {

    private var index = 0
    var device = devices[0]
        private set

    fun selectNextDevice(): Device {
        index = (index + 1) % devices.size
        device = devices[index]
        return device
    }

    fun plusRed(delta: Int) {
        (device as? RgbFeature)?.run { setColor(getColor().plusRed(delta)) }
    }

    fun plusGreen(delta: Int) {
        (device as? RgbFeature)?.run { setColor(getColor().plusGreen(delta)) }
    }

    fun plusBlue(delta: Int) {
        (device as? RgbFeature)?.run { setColor(getColor().plusBlue(delta)) }
    }

    fun plusWhite(delta: Int) {
        (device as? RgbwFeature)?.run { setColor(getColor().plusWhite(delta)) }
    }

    fun plusAmber(delta: Int) {
        (device as? RgbwauvFeature)?.run { setColor(getColor().plusAmber(delta)) }
    }

    fun plusUv(delta: Int) {
        (device as? RgbwauvFeature)?.run { setColor(getColor().plusUv(delta)) }
    }
}