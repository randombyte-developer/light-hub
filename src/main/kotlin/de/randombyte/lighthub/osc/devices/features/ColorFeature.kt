package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.show.flows.colorchanger.ColorCategoriesConfig
import de.randombyte.lighthub.show.tickables.AutoPatternsConfig

interface ColorFeature : Feature {
    fun getColor(): Color
    fun setColor(color: Color)

    open class ColorAutoPatternsConfig(
        `change-every-n-beats`: Int = 4,
        `change-beats-offset`: Int = 0
    ) : AutoPatternsConfig(`change-every-n-beats`, `change-beats-offset`) {
        companion object {
            const val FILE_NAME = "color-auto-pattern"
        }
    }

    val colors: Map<String, Color>
    val colorCategories get() = (type as Config).colorCategoriesConfig.config
    val colorAutoPatterns get() = (type as Config).colorAutoPatterns.config
    interface Config {
        val colorCategoriesConfig: ConfigHolder<out ColorCategoriesConfig>
        val colorAutoPatterns: ConfigHolder<out ColorAutoPatternsConfig>
    }
}