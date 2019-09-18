package de.randombyte.lighthub.osc.devices

open class MetaConfig(
    val qlcMeta: QlcMeta = QlcMeta(),
    val addresses: List<Int> = emptyList(),
    val `short-name`: String = ""
) {
    /**
     * Used in QLC+ to pick the DMX mapping.
     */
    class QlcMeta(
        val manufacturer: String = "",
        val model: String = "",
        val mode: String = "",
        val name: String = ""
    )

    companion object {
        const val FILE_NAME = "meta"
    }
}