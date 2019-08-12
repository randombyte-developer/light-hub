package de.randombyte.lighthub.osc

object QlcPlus : Receiver("QlcPlus") {

    val oscMasterDimmer = createOscChannel("master-dimmer", -1)
    val oscBlackout = createOscChannel("blackout", -1)

}