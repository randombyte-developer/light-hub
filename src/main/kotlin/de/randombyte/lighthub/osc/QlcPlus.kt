package de.randombyte.lighthub.osc

object QlcPlus : Receiver("QlcPlus") {

    val oscMasterDimmer = "MasterDimmer".toOscChannel()
    val oscBlackout = "Blackout".toOscChannel()

}