package de.randombyte.lighthub.show

import de.randombyte.lighthub.osc.devices.Device
import de.randombyte.lighthub.osc.devices.features.RgbFeature
import de.randombyte.lighthub.osc.devices.features.RgbwFeature
import de.randombyte.lighthub.osc.devices.features.RgbwauvFeature

/**
 * Set the color of each device separately.
 */
class AmbientManual(val devices: List<Device>) {

    private var index = 0
    private var device = devices[0]

    /**
     * @return the short name of the newly selected device/feature
     */
    fun selectNextDevice(): String {
        index = (index + 1) % devices.size
        device = devices[index]
        return device.type.metaConfigHolder.config.`short-name` + device.number
    }

    fun plusRed(delta: Int) {
        (device as? RgbFeature)?.run { rgb = rgb.plusRed(delta) }
    }

    fun plusGreen(delta: Int) {
        (device as? RgbFeature)?.run { rgb = rgb.plusGreen(delta) }
    }

    fun plusBlue(delta: Int) {
        (device as? RgbFeature)?.run { rgb = rgb.plusBlue(delta) }
    }

    fun plusWhite(delta: Int) {
        (device as? RgbwFeature)?.run { rgbw = rgbw.plusWhite(delta) }
    }

    fun plusAmber(delta: Int) {
        (device as? RgbwauvFeature)?.run { rgbwauv = rgbwauv.plusAmber(delta) }
    }

    fun plusUv(delta: Int) {
        (device as? RgbwauvFeature)?.run { rgbwauv = rgbwauv.plusUv(delta) }
    }
}