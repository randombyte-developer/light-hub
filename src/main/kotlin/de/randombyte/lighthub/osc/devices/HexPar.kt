package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.ConfigHolder.Companion.create
import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.osc.devices.features.Feature
import de.randombyte.lighthub.osc.devices.features.impl.MasterDimmerFeatureImpl
import de.randombyte.lighthub.osc.devices.features.impl.RgbwauvFeatureImpl
import de.randombyte.lighthub.osc.devices.features.impl.RgbwauvFeatureImpl.RgbwauvConfig
import de.randombyte.lighthub.utils.Ranges

class HexPar(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
) {

    companion object : Type<HexPar> {
        override val clazz = HexPar::class
        override val constructor = ::HexPar
        override val id = "hex-par"
        override val channels = 12

        override val metaConfigHolder = create<MetaConfig>(id, "meta")
        val colors = create<RgbwauvConfig>(id, "colors")

        override val configHolders = listOf(colors)

        private const val OSC_PROGRAM_DIMMING_MODE = 0
        private const val OSC_SHUTTER_LED_ON = 32
    }

    private val oscRed = "red".toOscChannel()
    private val oscGreen = "green".toOscChannel()
    private val oscBlue = "blue".toOscChannel()
    private val oscWhite = "white".toOscChannel()
    private val oscAmber = "amber".toOscChannel()
    private val oscUv = "uv".toOscChannel()
    private val oscMasterDimmer = "master-dimmer".toOscChannel()
    private val oscShutter = "shutter".toOscChannel()
    private val oscProgram = "program-selection".toOscChannel()
    private val oscMacro = "macro".toOscChannel()
    private val oscSpeed = "speed".toOscChannel()
    private val oscDimmerCurve = "dimmer-curve".toOscChannel()

    override val oscChannelMapping = OscChannelMapping(
        0 to oscRed,
        1 to oscGreen,
        2 to oscBlue,
        3 to oscWhite,
        4 to oscAmber,
        5 to oscUv,
        6 to oscMasterDimmer,
        7 to oscShutter,
        8 to oscProgram,
        9 to oscMacro,
        10 to oscSpeed,
        11 to oscDimmerCurve
    )

    val color = RgbwauvFeatureImpl(Companion, oscRed, oscGreen, oscBlue, oscWhite, oscAmber, oscUv)

    val masterDimmer = MasterDimmerFeatureImpl(oscMasterDimmer)

    override val features: List<Feature> = listOf(color, masterDimmer)

    fun dimmingMode() {
        oscProgram.sendValue(OSC_PROGRAM_DIMMING_MODE)
        oscShutter.sendValue(OSC_SHUTTER_LED_ON)
        masterDimmer.on()
    }
}