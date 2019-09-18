package de.randombyte.lighthub.osc.devices.features.colors

import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

open class Rgb(
    val red: Int = 0,
    val green: Int = 0,
    val blue: Int = 0
) : DimmableComponentsColor {

    init {
        require(red in DMX_RANGE && green in DMX_RANGE && blue in DMX_RANGE) {
            "Color components out of range! ($red, $green, $blue)"
        }
    }

    companion object {
        val default: Rgb = new(r = 0, g = 0, b = 0)

        fun new(r: Int, g: Int, b: Int) =
            Rgb(
                red = r.coerceIn(DMX_RANGE),
                green = g.coerceIn(DMX_RANGE),
                blue = b.coerceIn(DMX_RANGE)
            )
    }

    open fun plusRed(delta: Int) = new(red + delta, green, blue)
    open fun plusGreen(delta: Int) = new(red, green + delta, blue)
    open fun plusBlue(delta: Int) = new(red, green, blue + delta)

    override fun transformComponents(other: DimmableComponentsColor, transformer: (current: Int, other: Int) -> Int): Rgb {
        require(other is Rgb) { "'other' color needs to be at least Rgb too!" }
        return new(
            r = transformer(red, other.red),
            g = transformer(green, other.green),
            b = transformer(blue, other.blue)
        )
    }
}

open class RgbConfig(override val colors: Map<String, Rgb> = emptyMap()) : Color.Config()