package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.osc.devices.features.Feature
import de.randombyte.lighthub.osc.devices.features.MetaFeature
import de.randombyte.lighthub.osc.devices.features.colors.RgbwauvFeature
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

class HexPar(number: Int) : Device(
    type = Companion,
    oscBasePath = "HexPar",
    number = number
) {

    companion object : Type {
        override val id = "hex-par"
        override val channels = 12
    }

    private val oscRed = "Red".toOscChannel()
    private val oscGreen = "Green".toOscChannel()
    private val oscBlue = "Blue".toOscChannel()
    private val oscWhite = "White".toOscChannel()
    private val oscAmber = "Amber".toOscChannel()
    private val oscUv = "Uv".toOscChannel()
    private val oscMasterDimmer = "MasterDimmer".toOscChannel()
    private val oscShutter = "Shutter".toOscChannel()
    private val oscProgram = "ProgrammSelection".toOscChannel()
    private val oscMacro = "Macros".toOscChannel()
    private val oscSpeed = "Speed".toOscChannel()
    private val oscDimmerCurve = "DimmerCurve".toOscChannel()

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

    override val metaFeature = MetaFeature(Companion)

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