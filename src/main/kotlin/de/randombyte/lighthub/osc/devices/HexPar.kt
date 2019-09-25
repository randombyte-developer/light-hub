package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.createConfigHolder
import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.createOscChannel
import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.osc.devices.features.StrobeFeature.StrobeSpeedsConfig
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.osc.devices.features.colors.RgbwauvConfig
import de.randombyte.lighthub.show.flows.colorchanger.ColorCategoriesConfig

class HexPar(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
), RgbwauvFeatureImpl, MasterDimmerFeatureImpl, StrobeFeatureImpl {

    companion object : Type<HexPar>, RgbwauvFeature.Config, StrobeFeature.Config {
        override val clazz = HexPar::class
        override val constructor = ::HexPar
        override val id = "hex-par"
        override val channelsCount = 12

        override val metaConfig = createConfigHolder<MetaConfig>(MetaConfig.FILE_NAME)
        override val colors = createConfigHolder<RgbwauvConfig>(Color.Config.FILE_NAME)
        override val colorCategoriesConfig = createConfigHolder<ColorCategoriesConfig>(ColorCategoriesConfig.FILE_NAME)
        override val strobeSpeeds = createConfigHolder<StrobeSpeedsConfig>(StrobeSpeedsConfig.FILE_NAME)
        override val configs = listOf(colors, colorCategoriesConfig, strobeSpeeds)

        private const val OSC_PROGRAM_DIMMING_MODE = 0
        private const val OSC_SHUTTER_LED_ON = 32
    }

    override val oscRed = createOscChannel("red", 0)
    override val oscGreen = createOscChannel("green", 1)
    override val oscBlue = createOscChannel("blue", 2)
    override val oscWhite = createOscChannel("white", 3)
    override val oscAmber = createOscChannel("amber", 4)
    override val oscUv = createOscChannel("uv", 5)
    override val oscMasterDimmer = createOscChannel("master-dimmer", 6)
    private val oscShutter = createOscChannel("shutter", 7)
    private val oscProgram = createOscChannel("program-selection", 8)
    private val oscMacro = createOscChannel("macro", 9)
    private val oscProgramSpeed = createOscChannel("speed", 10)
    override val oscSpeed = oscShutter
    private val oscDimmerCurve = createOscChannel("dimmer-curve", 11)

    override val oscSpeedRange = 64..95
    override val oscNoStrobe = 32

    override val oscChannelList = OscChannelList(
        oscRed,
        oscGreen,
        oscBlue,
        oscWhite,
        oscAmber,
        oscUv,
        oscMasterDimmer,
        oscShutter,
        oscProgram,
        oscMacro,
        oscProgramSpeed,
        oscDimmerCurve
    )

    override var masterDimmer: Int
        get() = super.masterDimmer
        set(value) {
            super.masterDimmer = value
            oscProgram.sendValue(OSC_PROGRAM_DIMMING_MODE)
            oscShutter.sendValue(OSC_SHUTTER_LED_ON)
        }
}