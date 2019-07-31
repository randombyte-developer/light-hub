package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.config.ConfigHolder.Companion.create
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.colors.Rgbw
import de.randombyte.lighthub.osc.devices.features.colors.RgbwConfig

interface RgbwFeature : RgbFeature {
    var rgbw: Rgbw

    override val colors: ConfigHolder<out RgbwConfig>
}

interface RgbwFeatureImpl : RgbwFeature, RgbFeatureImpl {
    val oscWhite: OscChannel

    override val colors: ConfigHolder<out RgbwConfig> get() = create(type.id, "colors")

    override var rgbw: Rgbw
        get() = Rgbw.new(rgb, w = oscWhite.lastValue)
        set(value) {
            rgb = value
            oscWhite.sendValue(value.white)
        }
}