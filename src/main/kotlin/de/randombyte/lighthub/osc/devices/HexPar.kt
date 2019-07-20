package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.ConfigHolder.Companion.create
import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.osc.devices.features.Feature
import de.randombyte.lighthub.osc.devices.features.MetaConfig
import de.randombyte.lighthub.osc.devices.features.colors.RgbwauvFeature
import de.randombyte.lighthub.osc.devices.features.colors.RgbwauvFeature.RgbwauvConfig
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

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

    val rgbwauvFeature = RgbwauvFeature(Companion, oscRed, oscGreen, oscBlue, oscWhite, oscAmber, oscUv)

    override val features: List<Feature> = listOf(rgbwauvFeature)

    fun ledOn() {
        oscShutter.sendValue(32)
    }

    fun dimmingMode() {
        oscProgram.sendValue(0)
        ledOn()
        masterDimmer = DMX_RANGE.endInclusive
    }

    var masterDimmer: Int = DMX_RANGE.endInclusive
        set(value) {
            field = oscMasterDimmer.sendValue(value)
        }
}