package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.colors.Rgb
import de.randombyte.lighthub.osc.devices.features.colors.RgbConfig

interface RgbFeature : Feature {
    var rgb: Rgb

    val colors get() = (type as Config).colors.config.colors
    interface Config {
        val colors: ConfigHolder<RgbConfig>
    }
}

interface RgbFeatureImpl : RgbFeature {
    val oscRed: OscChannel
    val oscGreen: OscChannel
    val oscBlue: OscChannel

    override var rgb: Rgb
        get() = Rgb.new(r = oscRed.lastValue, g = oscGreen.lastValue, b = oscBlue.lastValue)
        set(value) {
            oscRed.sendValue(value.red)
            oscGreen.sendValue(value.green)
            oscBlue.sendValue(value.blue)
        }
}