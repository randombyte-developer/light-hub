package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.osc.devices.features.colors.FixedColor
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface FixedColorFeature : ColorFeature {
    override val colors: Map<String, FixedColor>
}

@ExperimentalTime
interface FixedColorFeatureImpl : FixedColorFeature {
    val oscColorSelection: OscChannel

    override fun getColor() = colors.values.first { oscColorSelection.lastValue in it.dmxRange }

    override fun setColor(color: Color) {
        if (color is FixedColor) oscColorSelection.sendValue(color.dmxRange.first)
    }
}