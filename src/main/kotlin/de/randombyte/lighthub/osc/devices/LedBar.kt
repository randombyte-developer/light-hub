package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.createConfigHolder
import de.randombyte.lighthub.osc.OscChannel.OscMultiChannel
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.createOscChannel
import de.randombyte.lighthub.osc.createOscDimmedChannel
import de.randombyte.lighthub.osc.devices.features.MasterDimmerFeature
import de.randombyte.lighthub.osc.devices.features.RgbFeatureImpl
import de.randombyte.lighthub.osc.devices.features.StrobeFeatureImpl
import de.randombyte.lighthub.utils.Ranges
import de.randombyte.lighthub.utils.coerceIn

class LedBar(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
), RgbFeatureImpl, MasterDimmerFeature, StrobeFeatureImpl {

    companion object : Type<LedBar> {
        override val clazz = LedBar::class
        override val constructor = ::LedBar
        override val id = "led-bar"
        override val channels = 11

        override val metaConfigHolder = createConfigHolder<MetaConfig>( "meta")

        private const val OSC_MODE_DIMMING = 41
        private const val OSC_SHUTTER_LED_ON = 0
    }

    override val configs get() = listOf(colors, strobeSpeeds)

    private val oscMode = createOscChannel("mode", 0)
    private val oscShutter = createOscChannel("shutter", 1)

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

    override val oscSpeed = oscShutter
    override val oscSpeedRange = 161..255

    override var masterDimmer: Int = Ranges.DMX_RANGE.last
        set(value) {
            field = value.coerceIn(Ranges.DMX_RANGE, "Setting master dimmer for LedBar")
            // resend last values to recalculate the master dimmer into the colors
            oscAllColors.forEach { it.sendValue(it.lastValue) }
        }

    override val oscChannelList = OscChannelList(
        oscMode,
        oscShutter,
        oscRed1,
        oscGreen1,
        oscBlue1,
        oscRed2,
        oscGreen2,
        oscBlue2,
        oscRed3,
        oscGreen3,
        oscBlue3
    )

    init {
        oscMode.sendValue(OSC_MODE_DIMMING)
        oscShutter.sendValue(OSC_SHUTTER_LED_ON)
        fullIntensity()
    }

    private fun createOscDimmedChannel(path: String, relativeDmxAddress: Int) =
        createOscDimmedChannel(path, relativeDmxAddress, ::masterDimmer)
}