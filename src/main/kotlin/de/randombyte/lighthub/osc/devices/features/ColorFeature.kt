package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.show.flows.colorchanger.ColorCategoriesConfig

interface ColorFeature : Feature {
    fun getColor(): Color
    fun setColor(color: Color)

    val colors get() = (type as Config).colors.config.colors
    val colorCategories get() = (type as Config).colorCategoriesConfig.config
    interface Config {
        val colors: ConfigHolder<Color.Config>
        val colorCategoriesConfig: ConfigHolder<ColorCategoriesConfig>
    }
}