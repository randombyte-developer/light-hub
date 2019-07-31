package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.Device
import de.randombyte.lighthub.osc.devices.features.colors.Rgb
import de.randombyte.lighthub.osc.devices.features.colors.RgbConfig

interface RgbFeature : ConfigurableFeature {
    var rgb: Rgb

    val colors: ConfigHolder<out RgbConfig>

    override val configs get() = listOf(colors)
}

interface RgbFeatureImpl : RgbFeature {
    val oscRed: OscChannel
    val oscGreen: OscChannel
    val oscBlue: OscChannel

    val type: Device.Type<*>

    // explicitly specifying the type here to not lose the `out` for the overriding interface RgbwFeatureImpl
    override val colors: ConfigHolder<out RgbConfig> get() = ConfigHolder.create(type.id, "colors")

    override var rgb: Rgb
        get() = Rgb.new(r = oscRed.lastValue, g = oscGreen.lastValue, b = oscBlue.lastValue)
        set(value) {
            oscRed.sendValue(value.red)
            oscGreen.sendValue(value.green)
            oscBlue.sendValue(value.blue)
        }
}