package de.randombyte.lighthub.qlc

import com.illposed.osc.OSCMessage
import com.illposed.osc.transport.udp.OSCPortOut
import de.randombyte.lighthub.dmx.DmxChannel
import java.net.InetAddress

object OscHandler {
    private val oscPort = OSCPortOut(InetAddress.getLoopbackAddress(), 7700)

    fun send(dmxChannel: DmxChannel, value: UByte) {
        oscPort.send(OSCMessage(dmxChannel.oscPath, listOf(value.toInt())))
    }
}