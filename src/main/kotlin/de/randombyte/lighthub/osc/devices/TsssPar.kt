package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.osc.devices.features.Feature
import de.randombyte.lighthub.osc.devices.features.MetaFeature
import de.randombyte.lighthub.osc.devices.features.colors.RgbwFeature
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

class TsssPar(number: Int) : Device(
    type = Companion,
    oscBasePath = "TsssPar",
    number = number
) {

    companion object : Type {
        override val id = "tsss-par"
        override val channels = 8
    }

    private val oscMode = "Mode".toOscChannel()
    private val oscColorMacro = "ColorMacro".toOscChannel()
    private val oscSpeed = "Speed".toOscChannel()
    private val oscMasterDimmer = "MasterDimmer".toOscChannel()
    private val oscRed = "Red".toOscChannel()
    private val oscGreen = "Green".toOscChannel()
    private val oscBlue = "Blue".toOscChannel()
    private val oscWhite = "White".toOscChannel()

    override val oscChannelMapping = OscChannelMapping(
        0 to oscMode,
        1 to oscColorMacro,
        2 to oscSpeed,
        3 to oscMasterDimmer,
        4 to oscRed,
        5 to oscGreen,
        6 to oscBlue,
        7 to oscWhite
    )

    val rgbwFeature = RgbwFeature(HexPar, oscRed, oscGreen, oscBlue, oscWhite)

    override val metaFeature = MetaFeature(Companion)

    override val features: List<Feature> = listOf(rgbwFeature)

    fun dimmingMode() {
        oscMode.sendValue(0)
        oscMasterDimmer.sendValue(DMX_RANGE.last)
    }

    fun blackout() {
        oscMode.sendValue(0)
    }
}