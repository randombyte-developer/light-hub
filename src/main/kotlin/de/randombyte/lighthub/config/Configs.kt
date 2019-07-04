package de.randombyte.lighthub.config

import de.randombyte.lighthub.config.loader.toConfigHolder
import de.randombyte.lighthub.config.serializer.CustomTypes
import de.randombyte.lighthub.osc.dmx.Device

object Configs {
    val general = "general.conf".toConfigHolder<GeneralConfig>()

    private val configs = listOf(general)

    fun setup() {
        CustomTypes.register()

        Device.types.forEach { it.configHolder.reload() }
        reload()
    }

    fun reload() {
        configs.forEach { it.reload() }
    }
}