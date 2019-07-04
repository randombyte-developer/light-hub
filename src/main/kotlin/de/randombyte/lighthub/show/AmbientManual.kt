package de.randombyte.lighthub.show

import de.randombyte.lighthub.config.Color
import de.randombyte.lighthub.osc.dmx.Light

/**
 * Most ugly class... type erasure ffs TODO: improve
 */
class AmbientManual(val rgbLights: List<Light<Color.Rgb>>, val rgbwLights: List<Light<Color.Rgbw>>, val rgbwauvLights: List<Light<Color.Rgbwauv>>) {

    var rgbIndex = 0
    var rgbwIndex = 0
    var rgbwauvIndex = 0

    var rgbLight: Light<Color.Rgb>? = rgbLights[0]
    var rgbwLight: Light<Color.Rgbw>? = null
    var rgbwauvLight: Light<Color.Rgbwauv>? = null

    enum class Switch { Rgb, Rgbw, Rgbwauv }
    var switch = Switch.Rgb

    fun plusRed(delta: Int) {
        rgbLight?.color = rgbLight!!.color.plusRed(delta)
        rgbwLight?.color = rgbwLight!!.color.plusRed(delta)
        rgbwauvLight?.color = rgbwauvLight!!.color.plusRed(delta)
    }

    fun plusGreen(delta: Int) {
        rgbLight?.color = rgbLight!!.color.plusGreen(delta)
        rgbwLight?.color = rgbwLight!!.color.plusGreen(delta)
        rgbwauvLight?.color = rgbwauvLight!!.color.plusGreen(delta)
    }

    fun plusBlue(delta: Int) {
        rgbLight?.color = rgbLight!!.color.plusBlue(delta)
        rgbwLight?.color = rgbwLight!!.color.plusBlue(delta)
        rgbwauvLight?.color = rgbwauvLight!!.color.plusBlue(delta)
    }

    fun plusWhite(delta: Int) {
        rgbwLight?.color = rgbwLight!!.color.plusWhite(delta)
        rgbwauvLight?.color = rgbwauvLight!!.color.plusWhite(delta)
    }

    fun plusAmber(delta: Int) {
        rgbwauvLight?.color = rgbwauvLight!!.color.plusAmber(delta)
    }

    fun plusUv(delta: Int) {
        rgbwauvLight?.color = rgbwauvLight!!.color.plusUv(delta)
    }

    fun get(): Light<out Color> {
        return when (switch) {
            Switch.Rgb -> rgbLight!!
            Switch.Rgbw -> rgbwLight!!
            Switch.Rgbwauv -> rgbwauvLight!!
        }
    }

    fun selectNext() {
        when (switch) {
            Switch.Rgb -> {
                rgbLight = rgbLights.getOrNull(rgbIndex)
                rgbIndex++
                if (rgbLight == null) {
                    switch = Switch.Rgbw
                    selectNext()
                    rgbIndex = 0
                }
            }
            Switch.Rgbw -> {
                rgbwLight = rgbwLights.getOrNull(rgbwIndex)
                rgbwIndex++
                if (rgbwLight == null) {
                    switch = Switch.Rgbwauv
                    selectNext()
                    rgbwIndex = 0
                }
            }
            Switch.Rgbwauv -> {
                rgbwauvLight = rgbwauvLights.getOrNull(rgbwauvIndex)
                rgbwauvIndex++
                if (rgbwauvLight == null) {
                    switch = Switch.Rgb
                    selectNext()
                    rgbwauvIndex = 0
                }
            }
        }
    }
}