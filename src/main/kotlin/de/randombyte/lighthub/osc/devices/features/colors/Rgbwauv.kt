package de.randombyte.lighthub.osc.devices.features.colors

import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

class Rgbwauv(
    red: Int = 0,
    green: Int = 0,
    blue: Int = 0,
    white: Int = 0,
    val amber: Int = 0,
    val uv: Int = 0
) : Rgbw(red, green, blue, white) {

    init {
        require(red in DMX_RANGE && green in DMX_RANGE && blue in DMX_RANGE && white in DMX_RANGE && amber in DMX_RANGE && uv in DMX_RANGE) {
            "Color components out of range! ($red, $green, $blue, $white, $amber, $uv)"
        }
    }

    companion object {
        val default: Rgbwauv = new(r = 0, g = 0, b = 0, w = 0, a = 0, uv = 0)

        fun new(r: Int, g: Int, b: Int, w: Int, a: Int, uv: Int) = Rgbwauv(
            red = r.coerceIn(DMX_RANGE),
            green = g.coerceIn(DMX_RANGE),
            blue = b.coerceIn(DMX_RANGE),
            white = w.coerceIn(DMX_RANGE),
            amber = a.coerceIn(DMX_RANGE),
            uv = uv.coerceIn(DMX_RANGE)
        )

        fun new(rgbw: Rgbw, a: Int, uv: Int) = new(
            r = rgbw.red,
            g = rgbw.green,
            b = rgbw.blue,
            w = rgbw.white,
            a = a,
            uv = uv
        )
    }

    override fun plusRed(delta: Int) = new(red + delta, green, blue, white, amber, uv)
    override fun plusGreen(delta: Int) = new(red, green + delta, blue, white, amber, uv)
    override fun plusBlue(delta: Int) = new(red, green, blue + delta, white, amber, uv)
    override fun plusWhite(delta: Int) = new(red, green, blue, white + delta, amber, uv)
    fun plusAmber(delta: Int) = new(red, green, blue, white, amber + delta, uv)
    fun plusUv(delta: Int) = new(red, green, blue, white, amber, uv + delta)
}

class RgbwauvConfig(override val colors: Map<String, Rgbwauv> = emptyMap()) : RgbwConfig()