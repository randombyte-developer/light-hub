package de.randombyte.lighthub.osc

import de.randombyte.lighthub.osc.OscChannel.OscDimmedChannel
import de.randombyte.lighthub.utils.CRC16
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE
import de.randombyte.lighthub.utils.coerceIn

open class OscChannel(val path: String, val relativeDmxAddress: Int) {

    val qlcChannel = CRC16.checksum(path.toCharArray())

    var lastValue: Int = 0
        protected set

    open fun sendValue(value: Int): Int {
        val coercedValue = value.coerceIn(DMX_RANGE, "Sending DMX value to OscChannel")

        Osc.send(path, coercedValue)
        lastValue = coercedValue

        return coercedValue
    }

    /**
     * Bundles many [channels] to one [OscChannel].
     */
    class OscMultiChannel(vararg val channels: OscChannel) : OscChannel(relativeDmxAddress = -1, path = "") {
        fun getAllNestedChannels(): List<OscChannel> = channels.flatMap {
            (it as? OscMultiChannel)?.getAllNestedChannels() ?: listOf(it)
        }

        override fun sendValue(value: Int): Int {
            val coercedValue = value.coerceIn(DMX_RANGE, "Sending DMX value to OscMultiChannel")
            channels.forEach { it.sendValue(coercedValue) }
            lastValue = coercedValue

            return coercedValue
        }
    }

    class OscDimmedChannel(path: String, relativeDmxAddress: Int, val masterDimmer: () -> Int) : OscChannel(path, relativeDmxAddress) {
        override fun sendValue(value: Int): Int {
            val coercedValue = value.coerceIn(DMX_RANGE, "Sending DMX value to OscDimmedChannel")
            val dimmedValue = (masterDimmer() / DMX_RANGE.last) * coercedValue

            Osc.send(path, dimmedValue)
            lastValue = coercedValue

            return coercedValue
        }
    }
}

fun Receiver.createOscChannel(path: String, relativeDmxAddress: Int) = OscChannel("/$oscBasePath/$path", relativeDmxAddress)

fun Receiver.createOscDimmedChannel(path: String, relativeDmxAddress: Int, masterDimmer: () -> Int) = OscDimmedChannel("/$oscBasePath/$path", relativeDmxAddress, masterDimmer)

class OscChannelList(vararg val channels: OscChannel) {
    val allNestedChannels = channels.flatMap {
        (it as? OscChannel.OscMultiChannel)?.getAllNestedChannels() ?: listOf(it)
    }
}