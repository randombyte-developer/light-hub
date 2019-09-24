package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.createConfigHolder
import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.createOscChannel
import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.osc.devices.features.StrobeFeature.StrobeSpeedsConfig
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.osc.devices.features.colors.RgbwConfig
import de.randombyte.lighthub.show.flows.colorchanger.ColorCategoriesConfig

class TsssPar(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
), RgbwFeatureImpl, MasterDimmerFeatureImpl, StrobeFeatureImpl {

    companion object : Type<TsssPar>, RgbwFeature.Config, StrobeFeature.Config {
        override val clazz = TsssPar::class
        override val constructor = ::TsssPar
        override val id = "tsss-par"

        override val metaConfig = createConfigHolder<MetaConfig>(MetaConfig.FILE_NAME)
        override val colors = createConfigHolder<RgbwConfig>(Color.Config.FILE_NAME)
        override val colorCategoriesConfig = createConfigHolder<ColorCategoriesConfig>(ColorCategoriesConfig.FILE_NAME)
        override val strobeSpeeds = createConfigHolder<StrobeSpeedsConfig>(StrobeSpeedsConfig.FILE_NAME)
        override val configs = listOf(colors, colorCategoriesConfig, strobeSpeeds)

        private const val OSC_MODE_DIMMING = 0
        private val OSC_MODE_STROBE_RANGE = 103..255
    }

    private val oscMode = createOscChannel("mode", 0)
    private val oscColorMacro = createOscChannel("color-macro", 1)
    override val oscSpeed = createOscChannel("speed", 2)
    override val oscMasterDimmer = createOscChannel("master-dimmer", 3)
    override val oscRed = createOscChannel("red", 4)
    override val oscGreen = createOscChannel("green", 5)
    override val oscBlue = createOscChannel("blue", 6)
    override val oscWhite = createOscChannel("white", 7)

    override val oscSpeedRange = 0..255
    override val oscNoStrobe = 0 // doesn't matter because of the custom implementation of strobing below

    // this light only strobes if a second channel activates it
    override var strobeSpeed: Double
        get() = super.strobeSpeed
        set(value) {
            super.strobeSpeed = value
            if (value > 0) {
                oscMode.sendValue(OSC_MODE_STROBE_RANGE.first) // activate strobe
            } else {
                oscMode.sendValue(OSC_MODE_DIMMING)
            }
        }

    // override default behavior because of the second strobe channel
    override val strobeActivated: Boolean
        get() = oscMode.lastValue in OSC_MODE_STROBE_RANGE

    override val oscChannelList = OscChannelList(
        oscMode,
        oscColorMacro,
        oscSpeed,
        oscMasterDimmer,
        oscRed,
        oscGreen,
        oscBlue,
        oscWhite
    )

    override var masterDimmer: Int
        get() = super.masterDimmer
        set(value) {
            super.masterDimmer = value
            oscMode.sendValue(OSC_MODE_DIMMING)
        }
}