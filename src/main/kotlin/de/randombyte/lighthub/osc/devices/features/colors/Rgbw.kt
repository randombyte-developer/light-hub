package de.randombyte.lighthub.osc.devices.features.colors

import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

open class Rgbw(
    red: Int = 0,
    green: Int = 0,
    blue: Int = 0,
    val white: Int = 0
) : Rgb(red, green, blue) {

    init {
        require(red in DMX_RANGE && green in DMX_RANGE && blue in DMX_RANGE && white in DMX_RANGE) {
            "Color components out of range! ($red, $green, $blue, $white)"
        }
    }

    companion object {
        val default: Rgbw = new(r = 0, g = 0, b = 0, w = 0)

        fun new(r: Int, g: Int, b: Int, w: Int) = Rgbw(
            red = r.coerceIn(DMX_RANGE),
            green = g.coerceIn(DMX_RANGE),
            blue = b.coerceIn(DMX_RANGE),
            white = w.coerceIn(DMX_RANGE)
        )

        fun new(rgb: Rgb, w: Int) = new(
            r = rgb.red,
            g = rgb.green,
            b = rgb.blue,
            w = w
        )
    }

    override fun plusRed(delta: Int) = new(red + delta, green, blue, white)
    override fun plusGreen(delta: Int) = new(red, green + delta, blue, white)
    override fun plusBlue(delta: Int) = new(red, green, blue + delta, white)
    open fun plusWhite(delta: Int) = new(red, green, blue, white + delta)

    override fun transformComponents(other: DimmableComponentsColor, transformer: (current: Int, other: Int) -> Int): Rgbw {
        require(other is Rgbw) { "'other' color needs to be at least Rgbw too!" }
        return new(
            rgb = super.transformComponents(other, transformer),
            w = transformer(white, other.white)
        )
    }
}

open class RgbwConfig(override val colors: Map<String, Rgbw> = emptyMap()) : RgbConfig()