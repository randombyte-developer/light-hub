package de.randombyte.lighthub.show

import de.randombyte.lighthub.osc.devices.Device
import de.randombyte.lighthub.osc.devices.features.colors.RgbFeature
import de.randombyte.lighthub.osc.devices.features.colors.RgbwFeature
import de.randombyte.lighthub.osc.devices.features.colors.RgbwauvFeature

/**
 * Set the color of each device separately.
 */
class AmbientManual(devices: List<Device>) {

    private val features: List<RgbFeature>
    private val deviceShortNames: List<String>

    init {
        val tmpFeatures= mutableListOf<RgbFeature>()
        val tmpDeviceShortNames = mutableListOf<String>()
        devices.forEach { device ->
            tmpFeatures += device.getFeaturesByType<RgbFeature>()
            tmpDeviceShortNames += device.metaFeature.configHolder.config.`short-name` + device.number
        }

        features = tmpFeatures
        deviceShortNames = tmpDeviceShortNames
    }

    private var selectedIndex = 0
    private var selectedFeature = features[0]

    /**
     * @return the short name of the newly selected device/feature
     */
    fun selectNextDevice(): String {
        selectedIndex = (selectedIndex + 1) % features.size
        selectedFeature = features[selectedIndex]
        return deviceShortNames[selectedIndex]
    }

    fun plusRed(delta: Int) {
        selectedFeature.rgb = selectedFeature.rgb.plusRed(delta)
    }

    fun plusGreen(delta: Int) {
        selectedFeature.rgb = selectedFeature.rgb.plusGreen(delta)
    }

    fun plusBlue(delta: Int) {
        selectedFeature.rgb = selectedFeature.rgb.plusBlue(delta)
    }

    fun plusWhite(delta: Int) {
        val feature = selectedFeature
        if (feature is RgbwFeature) {
            feature.rgb = feature.rgbw.plusWhite(delta)
        }
    }

    fun plusAmber(delta: Int) {
        val feature = selectedFeature
        if (feature is RgbwauvFeature) {
            feature.rgb = feature.rgbwauv.plusAmber(delta)
        }
    }

    fun plusUv(delta: Int) {
        val feature = selectedFeature
        if (feature is RgbwauvFeature) {
            feature.rgb = feature.rgbwauv.plusUv(delta)
        }
    }
}