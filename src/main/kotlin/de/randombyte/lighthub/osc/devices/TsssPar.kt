package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.ConfigHolder.Companion.create
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.devices.features.MasterDimmerFeatureImpl
import de.randombyte.lighthub.osc.devices.features.RgbwFeatureImpl
import de.randombyte.lighthub.osc.devices.features.StrobeFeatureImpl
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

class TsssPar(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
), RgbwFeatureImpl, MasterDimmerFeatureImpl, StrobeFeatureImpl {

    companion object : Type<TsssPar> {
        override val clazz = TsssPar::class
        override val constructor = ::TsssPar
        override val id = "tsss-par"
        override val channels = 8

        override val metaConfigHolder = create<MetaConfig>(id, "meta")

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

    // this light only strobes if a second channel activates it
    override var strobeSpeed: Double
        get() = super.strobeSpeed
        set(value) {
            super.strobeSpeed = value
            oscMode.sendValue(OSC_MODE_STROBE_RANGE.first) // activate strobe
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

    fun dimmingMode() {
        oscMode.sendValue(0)
        oscMasterDimmer.sendValue(DMX_RANGE.last)
    }

    fun blackout() {
        oscMode.sendValue(0)
    }
}