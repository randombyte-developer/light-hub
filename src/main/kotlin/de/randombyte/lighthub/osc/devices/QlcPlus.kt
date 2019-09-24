package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.Receiver
import de.randombyte.lighthub.osc.createOscChannel

object QlcPlus : Receiver("QlcPlus") {
    val oscMasterDimmer = createOscChannel("master-dimmer", -1)

    override val oscChannelList = OscChannelList(oscMasterDimmer)
}