package de.randombyte.lighthub.config

abstract class Color {

    companion object {
        val DEFAULT_COLOR_KEY = "red"
    }

    open class Rgb(
        val red: Int = 255,
        val green: Int = 255,
        val blue: Int = 255
    ) : Color() {
        companion object {
            fun new(r: Int, g: Int, b: Int) = Rgb(red = r, green = g, blue = b)
        }
    }

    class Rgbwauv(
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        val white: Int = 255,
        val amber: Int = 255,
        val uv: Int = 255
    ) : Rgb(red, green, blue) {
        companion object {
            fun new(
                r: Int,
                g: Int,
                b: Int,
                w: Int,
                a: Int,
                uv: Int
            ) = Rgbwauv(red = r, green = g, blue = b, white = w, amber = a, uv = uv)
        }
    }
}