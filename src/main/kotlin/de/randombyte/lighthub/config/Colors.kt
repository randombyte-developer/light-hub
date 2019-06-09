package de.randombyte.lighthub.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable abstract class Color {

    companion object {
        val DEFAULT_COLOR_KEY = "red"
    }

    @ConfigSerializable open class Rgb(
        @Setting val red: UByte = 255u,
        @Setting val green: UByte = 255u,
        @Setting val blue: UByte = 255u
    ) : Color() {
        companion object {
            fun new(r: UByte, g: UByte, b: UByte) = Rgb(red = r, green = g, blue = b)
        }
    }

    @ConfigSerializable class Rgbwauv(
        red: UByte = 255u,
        green: UByte = 255u,
        blue: UByte = 255u,
        @Setting val white: UByte = 255u,
        @Setting val amber: UByte = 255u,
        @Setting val uv: UByte = 255u
    ) : Rgb(red, green, blue) {
        companion object {
            fun new(
                r: UByte,
                g: UByte,
                b: UByte,
                w: UByte,
                a: UByte,
                uv: UByte
            ) = Rgbwauv(red = r, green = g, blue = b, white = w, amber = a, uv = uv)
        }
    }
}