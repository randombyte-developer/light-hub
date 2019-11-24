package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.devices.features.colors.DimmableComponentsColor
import de.randombyte.lighthub.show.flows.colorchanger.ColorSetsConfig
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface DimmableComponentsColorFeature : ColorFeature {
    override fun getColor(): DimmableComponentsColor

    override val colors get() = (type as Config).colors.config.colors
    override val colorSets get() = (type as Config).colorSetsConfig.config
    interface Config : ColorFeature.Config {
        val colors: ConfigHolder<out DimmableComponentsColor.Config>
        override val colorSetsConfig: ConfigHolder<out ColorSetsConfig>
    }
}