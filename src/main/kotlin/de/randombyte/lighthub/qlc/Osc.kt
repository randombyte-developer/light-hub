package de.randombyte.lighthub.qlc

import com.illposed.osc.OSCMessage
import com.illposed.osc.transport.udp.OSCPortOut
import java.net.InetAddress

object Osc {
    private val oscPort = OSCPortOut(InetAddress.getLoopbackAddress(), 7700)

    fun send(oscPath: String, value: Int) {
        oscPort.send(OSCMessage(oscPath, listOf(value)))
    }
}