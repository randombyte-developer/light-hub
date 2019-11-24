package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.show.ColorSetSelector
import de.randombyte.lighthub.show.flows.colorchanger.ColorChangerFlow
import de.randombyte.lighthub.show.flows.colorchanger.ColorSetsConfig
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface ColorFeature : Feature {
    fun getColor(): Color
    fun setColor(color: Color)
    fun setColor(colorId: String) {
        if (colorId == ColorChangerFlow.NONE_COLOR_ID) {
            (this as? ShutterFeature)?.noLight()
        } else {
            setColor(colors.getValue(colorId))
        }
    }

    val colors: Map<String, Color>
    val colorSets get() = (type as Config).colorSetsConfig.config
    val selectedColorSet get() = colorSets.all.getValue(ColorSetSelector.selectedColorSetId)
    interface Config {
        val colorSetsConfig: ConfigHolder<out ColorSetsConfig>
    }
}