package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.devices.Device

class MetaFeature(deviceType: Device.Type) : Feature.Configurable {

    val configHolder = ConfigHolder.create<Config>(deviceType.id, "meta")

    override val configHolders = listOf(configHolder)

    open class Config(
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
    }

}