package de.randombyte.lighthub.osc

object QlcPlus : Receiver("QlcPlus") {

    val oscMasterFader = "MasterFader".toOscChannel()
    val oscBlackout = "Blackout".toOscChannel()

}