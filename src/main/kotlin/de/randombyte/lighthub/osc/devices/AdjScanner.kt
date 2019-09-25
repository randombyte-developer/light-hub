package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.createConfigHolder
import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.createOscChannel
import de.randombyte.lighthub.osc.devices.features.AdjScannerColorWheelFeatureImpl

class AdjScanner(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
), AdjScannerColorWheelFeatureImpl {

    companion object : Type<AdjScanner> {
        override val clazz = AdjScanner::class
        override val constructor = ::AdjScanner
        override val id = "adj-scanner"
        override val channelsCount = 5

        override val metaConfig = createConfigHolder<MetaConfig>("meta")
    }

    private val oscPan = createOscChannel("pan", 0)
    private val oscTilt = createOscChannel("tilt", 1)
    override val oscColorWheel = createOscChannel("color-wheel", 2)
    private val oscGoboWheel = createOscChannel("gobo-wheel", 3)
    private val oscShutter = createOscChannel("shutter", 4)

    override val oscChannelList = OscChannelList(
        oscPan,
        oscTilt,
        oscColorWheel,
        oscGoboWheel,
        oscShutter
    )

    fun blackout() {
        oscShutter.sendValue(0)
    }
}