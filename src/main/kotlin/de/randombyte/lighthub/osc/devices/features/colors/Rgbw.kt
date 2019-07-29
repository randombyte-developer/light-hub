package de.randombyte.lighthub.osc.devices.features.colors

import de.randombyte.lighthub.utils.Ranges

open class Rgbw(
    red: Int = 0,
    green: Int = 0,
    blue: Int = 0,
    val white: Int = 0
) : Rgb(red, green, blue) {

    init {
        if (red !in Ranges.DMX_RANGE || green !in Ranges.DMX_RANGE || blue !in Ranges.DMX_RANGE || white !in Ranges.DMX_RANGE)
            throw IllegalArgumentException("Color components out of range! ($red, $green, $blue, $white)")
    }

    companion object {
        val default: Rgbw = new(r = 0, g = 0, b = 0, w = 0)

        fun new(r: Int, g: Int, b: Int, w: Int) = Rgbw(
            red = r.coerceIn(Ranges.DMX_RANGE),
            green = g.coerceIn(Ranges.DMX_RANGE),
            blue = b.coerceIn(Ranges.DMX_RANGE),
            white = w.coerceIn(Ranges.DMX_RANGE)
        )
    }

    override fun plusRed(delta: Int) = new(red + delta, green, blue, white)
    override fun plusGreen(delta: Int) = new(red, green + delta, blue, white)
    override fun plusBlue(delta: Int) = new(red, green, blue + delta, white)
    open fun plusWhite(delta: Int) = new(red, green, blue, white + delta)
}