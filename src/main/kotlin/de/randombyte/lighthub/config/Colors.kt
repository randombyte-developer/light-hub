package de.randombyte.lighthub.config

import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

abstract class Color {

    companion object {
        val DEFAULT_COLOR_KEY = "Red"
    }

    open class Rgb(
        val red: Int = 0,
        val green: Int = 0,
        val blue: Int = 0
    ) : Color() {

        init {
            if (red !in DMX_RANGE || green !in DMX_RANGE || blue !in DMX_RANGE)
            throw IllegalArgumentException("Color components out of range! ($red, $green, $blue)")
        }

        companion object {
            fun new(r: Int, g: Int, b: Int) = Rgb(
                red = r.coerceIn(DMX_RANGE),
                green = g.coerceIn(DMX_RANGE),
                blue = b.coerceIn(DMX_RANGE)
            )
        }

        open fun plusRed(delta: Int) = new(red + delta, green, blue)
        open fun plusGreen(delta: Int) = new(red, green + delta, blue)
        open fun plusBlue(delta: Int) = new(red, green, blue + delta)
    }

    open class Rgbw(
        red: Int = 0,
        green: Int = 0,
        blue: Int = 0,
        val white: Int = 0
    ): Rgb(red, green, blue) {

        init {
            if (red !in DMX_RANGE || green !in DMX_RANGE || blue !in DMX_RANGE || white !in DMX_RANGE)
                throw IllegalArgumentException("Color components out of range! ($red, $green, $blue, $white)")
        }

        companion object {
            fun new(
                r: Int,
                g: Int,
                b: Int,
                w: Int
            ) = Rgbw(
                red = r.coerceIn(DMX_RANGE),
                green = g.coerceIn(DMX_RANGE),
                blue = b.coerceIn(DMX_RANGE),
                white = w.coerceIn(DMX_RANGE)
            )
        }

        override fun plusRed(delta: Int) = new(red + delta, green, blue, white)
        override fun plusGreen(delta: Int) = new(red, green + delta, blue, white)
        override fun plusBlue(delta: Int) = new(red, green, blue + delta, white)
        open fun plusWhite(delta: Int) = new(red, green, blue, white + delta)
    }

    class Rgbwauv(
        red: Int = 0,
        green: Int = 0,
        blue: Int = 0,
        white: Int = 0,
        val amber: Int = 0,
        val uv: Int = 0
    ) : Rgbw(red, green, blue, white) {

        init {
            if (red !in DMX_RANGE || green !in DMX_RANGE || blue !in DMX_RANGE || white !in DMX_RANGE || amber !in DMX_RANGE || uv !in DMX_RANGE)
                throw IllegalArgumentException("Color components out of range! ($red, $green, $blue, $white, $amber, $uv)")
        }

        companion object {
            fun new(
                r: Int,
                g: Int,
                b: Int,
                w: Int,
                a: Int,
                uv: Int
            ) = Rgbwauv(
                red = r.coerceIn(DMX_RANGE),
                green = g.coerceIn(DMX_RANGE),
                blue = b.coerceIn(DMX_RANGE),
                white = w.coerceIn(DMX_RANGE),
                amber = a.coerceIn(DMX_RANGE),
                uv = uv.coerceIn(DMX_RANGE)
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