package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.osc.devices.features.colors.Rgbwauv
import de.randombyte.lighthub.osc.devices.features.colors.RgbwauvConfig
import de.randombyte.lighthub.show.flows.colorchanger.ColorCategoriesConfig

interface RgbwauvFeature : RgbwFeature {
    override fun getColor(): Rgbwauv

    // configs
    override val colors get() = (type as Config).colors.config.colors
    override val colorCategories get() = (type as Config).colorCategoriesConfig.config
    interface Config {
        val colors: ConfigHolder<RgbwauvConfig>
        val colorCategoriesConfig: ConfigHolder<ColorCategoriesConfig>
    }
}

interface RgbwauvFeatureImpl : RgbwauvFeature, RgbwFeatureImpl {
    val oscAmber: OscChannel
    val oscUv: OscChannel

    override fun getColor() = Rgbwauv.new(super.getColor(), a = oscAmber.lastValue, uv = oscUv.lastValue)
    override fun setColor(color: Color) {
        super.setColor(color)
        if (color is Rgbwauv) {
            oscAmber.sendValue(color.amber)
            oscUv.sendValue(color.uv)
        }
    }
}
