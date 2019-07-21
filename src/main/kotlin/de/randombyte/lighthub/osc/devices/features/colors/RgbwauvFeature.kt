package de.randombyte.lighthub.osc.devices.features.colors

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.Device
import de.randombyte.lighthub.osc.devices.Device.Companion.getByType
import de.randombyte.lighthub.utils.Ranges

class RgbwauvFeature(
    type: Device.Type<*>,
    oscRed: OscChannel,
    oscGreen: OscChannel,
    oscBlue: OscChannel,
    oscWhite: OscChannel,
    val oscAmber: OscChannel,
    val oscUv: OscChannel
) : RgbwFeature(type, oscRed, oscGreen, oscBlue, oscWhite) {

    private val colors = type.configHolders.getByType<RgbwauvConfig>()

    override var rgbw: Rgbw
        get() = rgbwauv
        set(value) {
            rgbwauv = Rgbwauv.new(r = value.red, g = value.green, b = value.blue, w = value.white, a = rgbwauv.amber, uv = rgbwauv.uv)
        }

    var rgbwauv: Rgbwauv = Rgbwauv.default
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

    class Rgbwauv(
        red: Int = 0,
        green: Int = 0,
        blue: Int = 0,
        white: Int = 0,
        val amber: Int = 0,
        val uv: Int = 0
    ) : RgbwFeature.Rgbw(red, green, blue, white) {

        init {
            if (red !in Ranges.DMX_RANGE || green !in Ranges.DMX_RANGE || blue !in Ranges.DMX_RANGE || white !in Ranges.DMX_RANGE || amber !in Ranges.DMX_RANGE || uv !in Ranges.DMX_RANGE)
                throw IllegalArgumentException("Color components out of range! ($red, $green, $blue, $white, $amber, $uv)")
        }

        companion object {
            val default: Rgbwauv = new(r = 0, g = 0, b = 0, w = 0, a = 0, uv = 0)

            fun new(r: Int, g: Int, b: Int, w: Int, a: Int, uv: Int) = Rgbwauv(
                red = r.coerceIn(Ranges.DMX_RANGE),
                green = g.coerceIn(Ranges.DMX_RANGE),
                blue = b.coerceIn(Ranges.DMX_RANGE),
                white = w.coerceIn(Ranges.DMX_RANGE),
                amber = a.coerceIn(Ranges.DMX_RANGE),
                uv = uv.coerceIn(Ranges.DMX_RANGE)
            )
        }

        override fun plusRed(delta: Int) = new(red + delta, green, blue, white, amber, uv)
        override fun plusGreen(delta: Int) = new(red, green + delta, blue, white, amber, uv)
        override fun plusBlue(delta: Int) = new(red, green, blue + delta, white, amber, uv)
        override fun plusWhite(delta: Int) = new(red, green, blue, white + delta, amber, uv)
        fun plusAmber(delta: Int) = new(red, green, blue, white, amber + delta, uv)
        fun plusUv(delta: Int) = new(red, green, blue, white, amber, uv + delta)
    }
}