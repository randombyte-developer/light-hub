package de.randombyte.lighthub.utils

/**
 * This has to match the algorithm QLC+ uses.
 */
object CRC16 {

    val table = listOf(
        0x0000, 0x1081, 0x2102, 0x3183,
        0x4204, 0x5285, 0x6306, 0x7387,
        0x8408, 0x9489, 0xa50a, 0xb58b,
        0xc60c, 0xd68d, 0xe70e, 0xf78f
    )

    fun checksum(data: CharArray): Int {
        var crc = 0xffff

        data.forEach { char ->
            crc = ((crc shr 4) and 0x0fff) xor table[((crc xor char.toInt())) and 15]
            val char2 = char.toInt() shr 4
            crc = ((crc shr 4) and 0x0fff) xor table[((crc xor char2)) and 15]
        }

        return crc.inv() and 0xffff
    }
}