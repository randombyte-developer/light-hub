package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.ConfigHolder.Companion.create
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.OscChannel.OscMultiChannel
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.devices.features.MasterDimmerFeatureImpl
import de.randombyte.lighthub.osc.devices.features.RgbFeatureImpl
import de.randombyte.lighthub.osc.devices.features.colors.RgbConfig
import de.randombyte.lighthub.utils.Ranges

class LedBar(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
), RgbFeatureImpl, MasterDimmerFeatureImpl {

    companion object : Type<LedBar> {
        override val clazz = LedBar::class
        override val constructor = ::LedBar
        override val id = "led-bar"
        override val channels = 11

        override val metaConfigHolder = create<MetaConfig>(id, "meta")
        val colors = create<RgbConfig>(id, "colors")
    }

    private val oscMode = createOscChannel("mode", 0)
    private val oscShutter = createOscChannel("shutter", 1)

    private val oscRed1 = createOscChannel("red-1", 2)
    private val oscGreen1 = createOscChannel("green-1", 3)
    private val oscBlue1 = createOscChannel("blue-1", 4)
    private val oscRed2 = createOscChannel("red-2", 5)
    private val oscGreen2 = createOscChannel("green-2", 6)
    private val oscBlue2 = createOscChannel("blue-2", 7)
    private val oscRed3 = createOscChannel("red-3", 8)
    private val oscGreen3 = createOscChannel("green-3", 9)
    private val oscBlue3 = createOscChannel("blue-3", 10)

    private val oscReds = OscMultiChannel(oscRed1, oscRed2, oscRed3)
    private val oscGreens = OscMultiChannel(oscGreen1, oscGreen2, oscGreen3)
    private val oscBlues = OscMultiChannel(oscBlue1, oscBlue2, oscBlue3)
    private val oscColorComponents = listOf(oscReds, oscGreens, oscBlues)

    override val oscRed = oscReds
    override val oscGreen = oscGreens
    override val oscBlue = oscBlues

    override val oscMasterDimmer: OscChannel = object : OscChannel("", -1) {
        override fun sendValue(value: Int): Int {
            oscColorComponents.forEach { component ->
                component.sendValue(Ranges.withMasterDimmer(component.lastValue, value))
            }
            lastValue = value
            return value
        }
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