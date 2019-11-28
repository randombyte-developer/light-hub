package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.Receiver
import de.randombyte.lighthub.osc.createOscChannel
import kotlin.time.ExperimentalTime

@ExperimentalTime
object QlcPlus : Receiver("QlcPlus") {
    val oscMasterDimmer = createOscChannel("master-dimmer", -1)

    override val oscChannelList = OscChannelList(oscMasterDimmer)
}