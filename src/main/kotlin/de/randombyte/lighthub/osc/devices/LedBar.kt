package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.ConfigHolder.Companion.create
import de.randombyte.lighthub.osc.OscChannel.OscMultiChannel
import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.osc.devices.features.Feature
import de.randombyte.lighthub.osc.devices.features.MetaConfig
import de.randombyte.lighthub.osc.devices.features.colors.RgbFeature
import de.randombyte.lighthub.osc.devices.features.colors.RgbFeature.RgbConfig

class LedBar(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
) {

    companion object : Type<LedBar> {
        override val clazz = LedBar::class
        override val constructor = ::LedBar
        override val id = "led-bar"
        override val channels = 11

        override val metaConfigHolder = create<MetaConfig>(id, "meta")
        val colors = create<RgbConfig>(id, "colors")

        override val configHolders = listOf(colors)
    }

    private val oscMode = "mode".toOscChannel()
    private val oscShutter = "shutter".toOscChannel()

    private val oscRed1 = "red-1".toOscChannel()
    private val oscGreen1 = "green-1".toOscChannel()
    private val oscBlue1 = "blue-1".toOscChannel()
    private val oscRed2 = "red-2".toOscChannel()
    private val oscGreen2 = "green-2".toOscChannel()
    private val oscBlue2 = "blue-2".toOscChannel()
    private val oscRed3 = "red-3".toOscChannel()
    private val oscGreen3 = "green-3".toOscChannel()
    private val oscBlue3 = "blue-3".toOscChannel()

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