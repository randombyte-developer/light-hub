package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.devices.features.colors.DimmableComponentsColor
import de.randombyte.lighthub.show.flows.colorchanger.ColorCategoriesConfig

interface DimmableComponentsColorFeature : ColorFeature {
    override fun getColor(): DimmableComponentsColor

    class DimmableComponentsColorAutoPatternsConfig(
        `change-every-n-beats`: Int = 4,
        `change-beats-offset`: Int = 0,
        val `change-ticks-duration`: Int = 10
    ) : ColorFeature.ColorAutoPatternsConfig(`change-every-n-beats`, `change-beats-offset`) {
        companion object {
            const val FILE_NAME = "color-auto-pattern"
        }
    }

    override val colors get() = (type as Config).colors.config.colors
    override val colorCategories get() = (type as Config).colorCategoriesConfig.config
    override val colorAutoPatterns get() = (type as Config).colorAutoPatterns.config
    interface Config : ColorFeature.Config {
        val colors: ConfigHolder<out DimmableComponentsColor.Config>
        override val colorCategoriesConfig: ConfigHolder<out ColorCategoriesConfig>
        override val colorAutoPatterns: ConfigHolder<out DimmableComponentsColorAutoPatternsConfig>
    }
}