package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.osc.devices.features.Feature
import de.randombyte.lighthub.osc.devices.features.MetaFeature
import de.randombyte.lighthub.osc.devices.features.colors.AdjScannerColorWheelFeature

class AdjScanner(number: Int) : Device(
    type = Companion,
    oscBasePath = "AdjScanner",
    number = number
) {

    companion object : Type {
        override val id = "adj-scanner"
        override val channels = 5
    }

    private val oscPan = "Pan".toOscChannel()
    private val oscTilt = "Tilt".toOscChannel()
    private val oscColorWheel = "ColorWheel".toOscChannel()
    private val oscGoboWheel = "GoboWheel".toOscChannel()
    private val oscShutter = "Shutter".toOscChannel()

    override val oscChannelMapping = OscChannelMapping(
        0 to oscPan,
        1 to oscTilt,
        2 to oscColorWheel,
        3 to oscGoboWheel,
        4 to oscShutter
    )

    val adjScannerColorWheelFeature = AdjScannerColorWheelFeature(oscColorWheel)

    override val metaFeature = MetaFeature(Companion)

    override val features: List<Feature> = listOf(adjScannerColorWheelFeature)

    fun blackout() {
        oscShutter.sendValue(0)
    }
}