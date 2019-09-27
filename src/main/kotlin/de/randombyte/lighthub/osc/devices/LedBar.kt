package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.createConfigHolder
import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.OscChannel.OscMultiChannel
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.createOscChannel
import de.randombyte.lighthub.osc.createOscDimmedChannel
import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.osc.devices.features.StrobeFeature.StrobeSpeedsConfig
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.osc.devices.features.colors.RgbConfig
import de.randombyte.lighthub.show.flows.colorchanger.ColorCategoriesConfig
import de.randombyte.lighthub.utils.Ranges
import de.randombyte.lighthub.utils.coerceIn

class LedBar(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
), RgbFeatureImpl, MasterDimmerFeature, StrobeFeatureImpl {

    companion object : Type<LedBar>, RgbFeature.Config, StrobeFeature.Config {
        override val clazz = LedBar::class
        override val constructor = ::LedBar
        override val id = "led-bar"
        override val channelsCount = 11

        override val metaConfig = createConfigHolder<MetaConfig>(MetaConfig.FILE_NAME)
        override val colors = createConfigHolder<RgbConfig>(Color.Config.FILE_NAME)
        override val colorCategoriesConfig = createConfigHolder<ColorCategoriesConfig>(ColorCategoriesConfig.FILE_NAME)
        override val strobeSpeeds = createConfigHolder<StrobeSpeedsConfig>(StrobeSpeedsConfig.FILE_NAME)
        override val configs = listOf(colors, colorCategoriesConfig, strobeSpeeds)

        private const val OSC_MODE_DIMMING = 41
        private const val OSC_SHUTTER_LED_ON = 0
    }

    private val oscMode = createOscChannel("mode", 0)
    private val oscShutter = createOscChannel("shutter", 1)

    // don't expose these channels, because snapshots only handle the channels which are present in [oscChannelList],
    // which are [oscRed], [oscGreen], [oscBlue]. Any changes to the separate channels are not detected in the [OscMultiChannel]s
    private val oscRed1 = createOscDimmedChannel("red-1", 2)
    private val oscGreen1 = createOscDimmedChannel("green-1", 3)
    private val oscBlue1 = createOscDimmedChannel("blue-1", 4)
    private val oscRed2 = createOscDimmedChannel("red-2", 5)
    private val oscGreen2 = createOscDimmedChannel("green-2", 6)
    private val oscBlue2 = createOscDimmedChannel("blue-2", 7)
    private val oscRed3 = createOscDimmedChannel("red-3", 8)
    private val oscGreen3 = createOscDimmedChannel("green-3", 9)
    private val oscBlue3 = createOscDimmedChannel("blue-3", 10)
    private val oscAllColors = listOf(oscRed1, oscRed2, oscRed3, oscGreen1, oscGreen2, oscGreen3, oscBlue1, oscBlue2, oscBlue3)

    private val oscReds = OscMultiChannel(oscRed1, oscRed2, oscRed3)
    private val oscGreens = OscMultiChannel(oscGreen1, oscGreen2, oscGreen3)
    private val oscBlues = OscMultiChannel(oscBlue1, oscBlue2, oscBlue3)

    override val oscRed = oscReds
    override val oscGreen = oscGreens
    override val oscBlue = oscBlues

    override val oscStrobeSpeed = oscShutter
    override val oscSpeedRange = 161..255
    override val oscNoStrobe = 0

    override var masterDimmer: Int = Ranges.DMX_RANGE.last
        set(value) {
            field = value.coerceIn(Ranges.DMX_RANGE, "Setting master dimmer for LedBar")
            // resend last values to recalculate the master dimmer into the colors
            oscAllColors.forEach { it.sendValue(it.lastValue) }
        }

    override val oscChannelList = OscChannelList(
        oscMode,
        oscShutter,
        oscRed,
        oscGreen,
        oscBlue
    )

    init {
        oscMode.sendValue(OSC_MODE_DIMMING)
        oscShutter.sendValue(OSC_SHUTTER_LED_ON)
        fullIntensity()
    }

    private fun createOscDimmedChannel(path: String, relativeDmxAddress: Int) =
        createOscDimmedChannel(path, relativeDmxAddress, ::masterDimmer)
}