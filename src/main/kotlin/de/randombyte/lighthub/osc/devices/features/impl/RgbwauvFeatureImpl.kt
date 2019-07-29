package de.randombyte.lighthub.osc.devices.features.impl

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.Device
import de.randombyte.lighthub.osc.devices.Device.Companion.getByType
import de.randombyte.lighthub.osc.devices.features.RgbwauvFeature
import de.randombyte.lighthub.osc.devices.features.colors.Rgbw
import de.randombyte.lighthub.osc.devices.features.colors.Rgbwauv
import de.randombyte.lighthub.utils.Ranges

class RgbwauvFeatureImpl(
    type: Device.Type<*>,
    oscRed: OscChannel,
    oscGreen: OscChannel,
    oscBlue: OscChannel,
    oscWhite: OscChannel,
    val oscAmber: OscChannel,
    val oscUv: OscChannel
) : RgbwFeatureImpl(type, oscRed, oscGreen, oscBlue, oscWhite), RgbwauvFeature {

    private val colors = type.configHolders.getByType<RgbwauvConfig>()

    override var rgbw: Rgbw
        get() = rgbwauv
        set(value) {
            rgbwauv = Rgbwauv.new(
                r = value.red,
                g = value.green,
                b = value.blue,
                w = value.white,
                a = rgbwauv.amber,
                uv = rgbwauv.uv
            )
        }

    override var rgbwauv: Rgbwauv = Rgbwauv.default
        set(value) {
            field = value
            sendOsc()
        }

    override fun sendOsc() {
        super.sendOsc()
        oscAmber.sendValue(rgbwauv.amber)
        oscUv.sendValue(rgbwauv.uv)
    }


    class RgbwauvConfig(val colors: Map<String, Rgbwauv> = emptyMap())
}