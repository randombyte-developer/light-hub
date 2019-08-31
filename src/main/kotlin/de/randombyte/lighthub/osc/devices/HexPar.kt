package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.createConfigHolder
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.createOscChannel
import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.osc.devices.features.StrobeFeature.StrobeSpeedConfig
import de.randombyte.lighthub.osc.devices.features.colors.RgbwauvConfig

class HexPar(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
), RgbwauvFeatureImpl, MasterDimmerFeatureImpl, StrobeFeatureImpl {

    companion object : Type<HexPar>, RgbwauvFeature.Config, StrobeFeature.Config {
        override val clazz = HexPar::class
        override val constructor = ::HexPar
        override val id = "hex-par"
        override val channels = 12

        override val metaConfigHolder = createConfigHolder<MetaConfig>("meta")
        override val colors = createConfigHolder<RgbwauvConfig>("colors")
        override val strobeSpeeds = createConfigHolder<StrobeSpeedConfig>("strobe-speeds")
        override val configs = listOf(colors, strobeSpeeds)

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
    override val oscSpeed = createOscChannel("speed", 10)
    private val oscDimmerCurve = createOscChannel("dimmer-curve", 11)

    override val oscSpeedRange = 64..95

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
        oscSpeed,
        oscDimmerCurve
    )

    fun dimmingMode() {
        oscProgram.sendValue(OSC_PROGRAM_DIMMING_MODE)
        oscShutter.sendValue(OSC_SHUTTER_LED_ON)
        fullIntensity()
    }
}