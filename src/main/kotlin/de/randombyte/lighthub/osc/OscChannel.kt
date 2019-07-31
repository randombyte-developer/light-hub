package de.randombyte.lighthub.osc

import de.randombyte.lighthub.utils.CRC16
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

open class OscChannel(val path: String, val relativeDmxAddress: Int) {

    companion object {
        fun coerceValue(value: Int) = if (value in DMX_RANGE) value else {
            val coercedValue = value.coerceIn(DMX_RANGE)
            println("[Warning] OscChannel#sendValue(value) value($value) not in DMX range! Coercing to $coercedValue!")
            coercedValue
        }
    }

    val qlcChannel = CRC16.checksum(path.toCharArray())

    var lastValue: Int = 0
        protected set

    open fun sendValue(value: Int): Int {
        val coercedValue = coerceValue(value)

        Osc.send(path, coercedValue)
        lastValue = coercedValue

        return coercedValue
    }

    data class Snapshot(val relativeDmxAddress: Int, val value: Int)

    val snapshot: Snapshot get() = Snapshot(relativeDmxAddress = relativeDmxAddress, value = lastValue)

    /**
     * Bundles many [channels] to one [OscChannel].
     */
    class OscMultiChannel(vararg val channels: OscChannel) : OscChannel(relativeDmxAddress = -1, path = "") {
        override fun sendValue(value: Int): Int {
            val coercedValue = coerceValue(value)
            channels.forEach { Osc.send(it.path, coercedValue) }
            lastValue = coercedValue

            return coercedValue
        }
    }
}

class OscChannelList(vararg val channels: OscChannel) {
    class Snapshot(val snapshots: List<OscChannel.Snapshot>)

    val snapshot: Snapshot
        get() = Snapshot(channels.map { it.snapshot })

    fun restore(snapshot: Snapshot) {
        snapshot.snapshots.forEach { (address, value) ->
            channels.firstOrNull { it.relativeDmxAddress == address }?.sendValue(value)
        }
    }
}