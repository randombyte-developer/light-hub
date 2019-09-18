package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.devices.features.colors.DimmableComponentsColor
import de.randombyte.lighthub.show.flows.colorchanger.ColorCategoriesConfig

interface DimmableComponentsColorFeature : ColorFeature {
    override fun getColor(): DimmableComponentsColor

    // configs
    override val colors get() = (type as Config).colors.config.colors
    override val colorCategories get() = (type as Config).colorCategoriesConfig.config
    interface Config {
        val colors: ConfigHolder<DimmableComponentsColor.Config>
        val colorCategoriesConfig: ConfigHolder<ColorCategoriesConfig>
    }
}