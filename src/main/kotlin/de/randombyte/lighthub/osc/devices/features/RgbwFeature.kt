package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.osc.devices.features.colors.Rgbw
import de.randombyte.lighthub.osc.devices.features.colors.RgbwConfig
import de.randombyte.lighthub.show.flows.colorchanger.ColorSetsConfig
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface RgbwFeature : RgbFeature {
    override fun getColor(): Rgbw

    // configs
    override val colors get() = (type as Config).colors.config.colors
    override val colorSets get() = (type as Config).colorSetsConfig.config
    interface Config : RgbFeature.Config {
        override val colors: ConfigHolder<out RgbwConfig>
        override val colorSetsConfig: ConfigHolder<out ColorSetsConfig>
    }
}

@ExperimentalTime
interface RgbwFeatureImpl : RgbwFeature, RgbFeatureImpl {
    val oscWhite: OscChannel

    override fun getColor() = Rgbw.new(super.getColor(), w = oscWhite.lastValue)
    override fun setColor(color: Color) {
        super<RgbFeatureImpl>.setColor(color)
        if (color is Rgbw) oscWhite.sendValue(color.white)
    }
}