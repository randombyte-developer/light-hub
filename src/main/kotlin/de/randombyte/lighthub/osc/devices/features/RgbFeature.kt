package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.osc.devices.features.colors.Rgb
import de.randombyte.lighthub.osc.devices.features.colors.RgbConfig
import de.randombyte.lighthub.show.flows.colorchanger.ColorCategoriesConfig

interface RgbFeature : DimmableComponentsColorFeature {
    override fun getColor(): Rgb

    // configs
    override val colors get() = (type as Config).colors.config.colors
    override val colorCategories get() = (type as Config).colorCategoriesConfig.config
    interface Config : DimmableComponentsColorFeature.Config {
        override val colors: ConfigHolder<out RgbConfig>
        override val colorCategoriesConfig: ConfigHolder<out ColorCategoriesConfig>
    }
}

interface RgbFeatureImpl : RgbFeature {
    val oscRed: OscChannel
    val oscGreen: OscChannel
    val oscBlue: OscChannel

    override fun getColor() = Rgb.new(r = oscRed.lastValue, g = oscGreen.lastValue, b = oscBlue.lastValue)
    override fun setColor(color: Color) {
        if (color is Rgb) {
            oscRed.sendValue(color.red)
            oscGreen.sendValue(color.green)
            oscBlue.sendValue(color.blue)
        }
    }
}