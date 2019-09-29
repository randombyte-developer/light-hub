package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.osc.devices.features.colors.FixedColor
import de.randombyte.lighthub.show.flows.colorchanger.ColorCategoriesConfig

interface FixedColorFeature : ColorFeature {

    override val colorCategories get() = (type as Config).colorCategoriesConfig.config
    interface Config {
        val colorCategoriesConfig: ConfigHolder<ColorCategoriesConfig>
    }
}

interface FixedColorFeatureImpl : FixedColorFeature {

    override val colors: Map<String, FixedColor>

    val oscColorSelection: OscChannel

    override fun getColor() = colors.values.first() { oscColorSelection.lastValue in it.dmxRange }

    override fun setColor(color: Color) {
        if (color is FixedColor) oscColorSelection.sendValue(color.dmxRange.first)
    }
}