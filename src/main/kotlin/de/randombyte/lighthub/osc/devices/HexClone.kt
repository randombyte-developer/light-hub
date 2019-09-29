package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.createConfigHolder
import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.createOscChannel
import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.osc.devices.features.DimmableComponentsColorFeature.DimmableComponentsColorAutoPatternsConfig
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.osc.devices.features.colors.RgbwauvConfig
import de.randombyte.lighthub.show.flows.colorchanger.ColorCategoriesConfig

class HexClone(number: Int, dmxAddress: Int) : Device(
        type = Companion,
        number = number,
        dmxAddress = dmxAddress
), RgbwauvFeatureImpl, MasterDimmerFeatureImpl, StrobeFeatureImpl {

    companion object : Type<HexClone>, RgbwauvFeature.Config, StrobeFeature.Config {
        override val clazz = HexClone::class
        override val constructor = ::HexClone
        override val id = "hex-clone"
        override val channelsCount = 10

        override val metaConfig = createConfigHolder<MetaConfig>(MetaConfig.FILE_NAME)
        override val colors = createConfigHolder<RgbwauvConfig>(Color.Config.FILE_NAME)
        override val colorCategoriesConfig = createConfigHolder<ColorCategoriesConfig>(ColorCategoriesConfig.FILE_NAME)
        override val colorAutoPatterns = createConfigHolder<DimmableComponentsColorAutoPatternsConfig>(DimmableComponentsColorAutoPatternsConfig.FILE_NAME)
        override val strobeSpeeds = createConfigHolder<StrobeFeature.StrobeSpeedsConfig>(StrobeFeature.StrobeSpeedsConfig.FILE_NAME)
        override val configs = listOf(colors, colorCategoriesConfig, colorAutoPatterns, strobeSpeeds)
    }

    override val oscMasterDimmer = createOscChannel("master-dimmer", 0)
    override val oscRed = createOscChannel("red", 1)
    override val oscGreen = createOscChannel("green", 2)
    override val oscBlue = createOscChannel("blue", 3)
    override val oscWhite = createOscChannel("white", 4)
    override val oscAmber = createOscChannel("amber", 5)
    override val oscUv = createOscChannel("uv", 6)
    override val oscStrobeSpeed = createOscChannel("strobe-speed", 7)
    private val oscProgram = createOscChannel("program-selection", 8)
    private val oscProgramSpeed = createOscChannel("program-speed", 9)

    override val oscSpeedRange = 1..255
    override val oscNoStrobe = 0

    override val oscChannelList = OscChannelList(
        oscMasterDimmer,
        oscRed,
        oscGreen,
        oscBlue,
        oscWhite,
        oscAmber,
        oscUv,
        oscStrobeSpeed,
        oscProgram,
        oscProgramSpeed
    )
}