package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.osc.OscChannel.OscMultiChannel
import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.osc.devices.features.Feature
import de.randombyte.lighthub.osc.devices.features.MetaFeature
import de.randombyte.lighthub.osc.devices.features.colors.RgbFeature

class LedBar(number: Int) : Device(
    type = Companion,
    oscBasePath = "LedBar",
    number = number
) {

    companion object : Type {
        override val id = "led-bar"
        override val channels = 11
    }

    private val oscMode = "Mode".toOscChannel()
    private val oscShutter = "Shutter".toOscChannel()

    private val oscRed1 = "Red1".toOscChannel()
    private val oscGreen1 = "Green1".toOscChannel()
    private val oscBlue1 = "Blue1".toOscChannel()
    private val oscRed2 = "Red2".toOscChannel()
    private val oscGreen2 = "Green2".toOscChannel()
    private val oscBlue2 = "Blue2".toOscChannel()
    private val oscRed3 = "Red3".toOscChannel()
    private val oscGreen3 = "Green3".toOscChannel()
    private val oscBlue3 = "Blue3".toOscChannel()

    private val oscReds = OscMultiChannel(oscRed1, oscRed2, oscRed3)
    private val oscGreens = OscMultiChannel(oscGreen1, oscGreen2, oscGreen3)
    private val oscBlues = OscMultiChannel(oscBlue1, oscBlue2, oscBlue3)

    override val oscChannelMapping = OscChannelMapping(
        0 to oscMode,
        1 to oscShutter,
        2 to oscRed1,
        3 to oscGreen1,
        4 to oscBlue1,
        5 to oscRed2,
        6 to oscGreen2,
        7 to oscBlue2,
        8 to oscRed3,
        9 to oscGreen3,
        10 to oscBlue3
    )

    val rgbFeature = RgbFeature(Companion, oscReds, oscGreens, oscBlues)

    override val metaFeature = MetaFeature(Companion)

    override val features: List<Feature> = listOf(rgbFeature)

    fun ledOn() {
        oscMode.sendValue(41)
        oscShutter.sendValue(0)
    }

    fun strobe() {
        ledOn()
        oscShutter.sendValue(200)
    }

    fun blackout() {
        oscMode.sendValue(0)
    }
}