package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.show.ColorSelector
import de.randombyte.lighthub.show.flows.colorchanger.ColorSetsConfig
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface ColorFeature : Feature {
    fun getColor(): Color
    fun setColor(color: Color)
    fun setColor(colorId: String) = setColor(colors.getValue(colorId))

    val colors: Map<String, Color>
    val colorSets get() = (type as Config).colorSetsConfig.config
    val selectedColorSet get() = colorSets.all.getValue(ColorSelector.selectedColorSetId)
    interface Config {
        val colorSetsConfig: ConfigHolder<out ColorSetsConfig>
    }
}