package de.randombyte.lighthub.osc.devices.features.impl

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.Device
import de.randombyte.lighthub.osc.devices.Device.Companion.getByType
import de.randombyte.lighthub.osc.devices.features.Feature
import de.randombyte.lighthub.osc.devices.features.RgbFeature
import de.randombyte.lighthub.osc.devices.features.colors.Rgb
import de.randombyte.lighthub.utils.Ranges

open class RgbFeatureImpl(
    type: Device.Type<*>,
    val oscRed: OscChannel,
    val oscGreen: OscChannel,
    val oscBlue: OscChannel
) : RgbFeature {

    private val colors = type.configHolders.getByType<RgbConfig>()

    override var rgb: Rgb = Rgb.default
        set(value) {
            field = value
            sendOsc()
        }

    protected open fun sendOsc() {
        oscRed.sendValue(rgb.red)
        oscGreen.sendValue(rgb.green)
        oscBlue.sendValue(rgb.blue)
    }


    class RgbConfig(val colors: Map<String, Rgb> = emptyMap())
}