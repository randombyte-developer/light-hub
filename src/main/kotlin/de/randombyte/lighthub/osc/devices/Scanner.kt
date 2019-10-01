package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.createConfigHolder
import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.createOscChannel
import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.osc.devices.features.ColorFeature.ColorAutoPatternsConfig
import de.randombyte.lighthub.osc.devices.features.PanTiltFeature.PanTiltAutoPatternsConfig
import de.randombyte.lighthub.osc.devices.features.colors.ScannerColor
import de.randombyte.lighthub.show.flows.colorchanger.ColorSetsConfig

class Scanner(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
), FixedColorFeatureImpl, ShutterFeatureImpl, PanTiltFeatureImpl {

    companion object : Type<Scanner>, ColorFeature.Config, PanTiltFeature.Config {
        override val clazz = Scanner::class
        override val constructor = ::Scanner
        override val id = "adj-scanner"
        override val channelsCount = 5

        override val metaConfig = createConfigHolder<MetaConfig>(MetaConfig.FILE_NAME)
        override val colorSetsConfig = createConfigHolder<ColorSetsConfig>(ColorSetsConfig.FILE_NAME)
        override val colorAutoPatterns = createConfigHolder<ColorAutoPatternsConfig>(ColorAutoPatternsConfig.FILE_NAME)
        override val panTiltAutoPatterns = createConfigHolder<PanTiltAutoPatternsConfig>(PanTiltAutoPatternsConfig.FILE_NAME)
        override val configs = listOf(colorSetsConfig, colorAutoPatterns, panTiltAutoPatterns)

        private const val OSC_GOBO_OPEN = 0
    }

    override val oscPan = createOscChannel("pan", 0)
    override val oscTilt = createOscChannel("tilt", 1)
    override val oscColorSelection = createOscChannel("color-wheel", 2)
    private val oscGoboSelection = createOscChannel("gobo-wheel", 3)
    override val oscShutter = createOscChannel("shutter", 4)

    override val colors = ScannerColor.colors

    override val oscChannelList = OscChannelList(
        oscPan,
        oscTilt,
        oscColorSelection,
        oscGoboSelection,
        oscShutter
    )

    init {
        oscGoboSelection.sendValue(OSC_GOBO_OPEN)
    }
}