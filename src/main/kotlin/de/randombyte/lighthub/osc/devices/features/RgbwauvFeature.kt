package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.colors.Rgbwauv
import de.randombyte.lighthub.osc.devices.features.colors.RgbwauvConfig

interface RgbwauvFeature : RgbwFeature {
    var rgbwauv: Rgbwauv

    override val colors get() = (type as Config).colors.config.colors
    interface Config {
        val colors: ConfigHolder<RgbwauvConfig>
    }
}

interface RgbwauvFeatureImpl : RgbwauvFeature, RgbwFeatureImpl {
    val oscAmber: OscChannel
    val oscUv: OscChannel

    override var rgbwauv: Rgbwauv
        get() = Rgbwauv.new(rgbw, a = oscAmber.lastValue, uv = oscUv.lastValue)
        set(value) {
            rgbw = value
            oscAmber.sendValue(value.amber)
            oscUv.sendValue(value.uv)
        }
}
