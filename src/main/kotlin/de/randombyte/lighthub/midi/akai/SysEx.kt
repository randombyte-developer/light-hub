package de.randombyte.lighthub.midi.akai

object SysEx {
    private val SYSEX_START = ubyteArrayOf(0xF0u, 0x47u, 0x0u, 0x78u)
    private const val SYSEX_END = 0xF7.toByte()

    private val SIGNAL_VALID_STATUS = ubyteArrayOf(0x40u, 0x41u, 0x43u)

    val SYSEX_SPECIAL_MODE = ubyteArrayOf(0xF0u, 0x47u, 0x00u, 0x78u, 0x30u, 0x00u, 0x04u, 0x01u, 0x00u, 0x00u, 0x38u, 0xF7u)

    data class Signal constructor(val type: Int, val control: Int, val value: Int)

    fun parseSysEx(rawData: ByteArray): Signal? {
        if (rawData.size != 10) return null
        val uData = rawData.toUByteArray()
        if (!uData.containsAtFront(SYSEX_START) || rawData.last() != SYSEX_END) return null

        if (uData[4] !in SIGNAL_VALID_STATUS) {
            println("Invalid type: ${uData[4]}")
            return null
        }

        return Signal(uData[4].toInt(), uData[7].toInt(), uData[8].toInt())
    }

    private fun UByteArray.containsAtFront(data: UByteArray): Boolean {
        if (this.size < data.size) return false
        data.forEachIndexed { index, value ->
            if (get(index) != value) return false
        }
        return true
    }
}