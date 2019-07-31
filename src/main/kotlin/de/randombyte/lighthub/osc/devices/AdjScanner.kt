package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.devices.features.impl.AdjScannerColorWheelFeatureImpl

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
    }

    private val oscPan = createOscChannel("pan", 0)
    private val oscTilt = createOscChannel("tilt", 1)
    private val oscColorWheel = createOscChannel("color-wheel", 2)
    private val oscGoboWheel = createOscChannel("gobo-wheel", 3)
    private val oscShutter = createOscChannel("shutter", 4)

    override val oscChannelList = OscChannelList(
        oscPan,
        oscTilt,
        oscColorWheel,
        oscGoboWheel,
        oscShutter
    )

    val adjScannerColorWheel = AdjScannerColorWheelFeatureImpl(oscColorWheel)

    fun blackout() {
        oscShutter.sendValue(0)
    }
}