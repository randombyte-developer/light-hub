package de.randombyte.lighthub.osc.devices.features.colors

import de.randombyte.lighthub.utils.Ranges

open class Rgb(
    val red: Int = 0,
    val green: Int = 0,
    val blue: Int = 0
) {

    init {
        if (red !in Ranges.DMX_RANGE || green !in Ranges.DMX_RANGE || blue !in Ranges.DMX_RANGE)
            throw IllegalArgumentException("Color components out of range! ($red, $green, $blue)")
    }

    companion object {
        val default: Rgb = new(r = 0, g = 0, b = 0)

        fun new(r: Int, g: Int, b: Int) =
            Rgb(
                red = r.coerceIn(Ranges.DMX_RANGE),
                green = g.coerceIn(Ranges.DMX_RANGE),
                blue = b.coerceIn(Ranges.DMX_RANGE)
            )
    }

    open fun plusRed(delta: Int) = new(red + delta, green, blue)
    open fun plusGreen(delta: Int) = new(red, green + delta, blue)
    open fun plusBlue(delta: Int) = new(red, green, blue + delta)
}

open class RgbConfig(open val colors: Map<String, Rgb> = emptyMap())