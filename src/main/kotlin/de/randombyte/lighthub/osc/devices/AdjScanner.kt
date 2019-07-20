package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.osc.devices.features.Feature
import de.randombyte.lighthub.osc.devices.features.MetaConfig
import de.randombyte.lighthub.osc.devices.features.colors.AdjScannerColorWheelFeature

class AdjScanner(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
) {

    companion object : Type<AdjScanner> {
        override val clazz = AdjScanner::class
        override val constructor = ::AdjScanner
        override val id = "adj-scanner"
        override val channels = 5

        override val metaConfigHolder = ConfigHolder.create<MetaConfig>(id, "meta")

        override val configHolders: List<ConfigHolder<*>> = emptyList()
    }

    private val oscPan = "pan".toOscChannel()
    private val oscTilt = "tilt".toOscChannel()
    private val oscColorWheel = "color-wheel".toOscChannel()
    private val oscGoboWheel = "gobo-wheel".toOscChannel()
    private val oscShutter = "shutter".toOscChannel()

    override val oscChannelMapping = OscChannelMapping(
        0 to oscPan,
        1 to oscTilt,
        2 to oscColorWheel,
        3 to oscGoboWheel,
        4 to oscShutter
    )

    val adjScannerColorWheelFeature = AdjScannerColorWheelFeature(oscColorWheel)

    override val features: List<Feature> = listOf(adjScannerColorWheelFeature)

    fun blackout() {
        oscShutter.sendValue(0)
    }
}