package de.randombyte.lighthub.osc

import com.illposed.osc.OSCMessage
import com.illposed.osc.transport.udp.OSCPortOut
import java.net.InetAddress

object Osc {
    private val oscPortOut = OSCPortOut(InetAddress.getLoopbackAddress(), 7700)

    fun send(oscPath: String, value: Int) {
        oscPortOut.send(OSCMessage(oscPath, listOf(value)))
    }
}