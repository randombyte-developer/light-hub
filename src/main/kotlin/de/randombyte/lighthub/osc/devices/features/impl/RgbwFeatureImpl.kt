package de.randombyte.lighthub.osc.devices.features.impl

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.Device
import de.randombyte.lighthub.osc.devices.Device.Companion.getByType
import de.randombyte.lighthub.osc.devices.features.RgbwFeature
import de.randombyte.lighthub.osc.devices.features.colors.Rgb
import de.randombyte.lighthub.osc.devices.features.colors.Rgbw

open class RgbwFeatureImpl(
    type: Device.Type<*>,
    oscRed: OscChannel,
    oscGreen: OscChannel,
    oscBlue: OscChannel,
    val oscWhite: OscChannel
) : RgbFeatureImpl(type, oscRed, oscGreen, oscBlue), RgbwFeature {

    private val colors = type.configHolders.getByType<RgbwConfig>()

    // the super-class is never called, the field rgbw is actually storing the information
    override var rgb: Rgb
        get() = rgbw
        set(value) {
            rgbw = Rgbw.new(r = value.red, g = value.green, b = value.blue, w = rgbw.white)
        }

    override var rgbw: Rgbw = Rgbw.default
        set(value) {
            field = value
            sendOsc()
        }

    override fun sendOsc() {
        super.sendOsc()
        oscWhite.sendValue(rgbw.white)
    }


    class RgbwConfig(val colors: Map<String, Rgbw> = emptyMap())
}