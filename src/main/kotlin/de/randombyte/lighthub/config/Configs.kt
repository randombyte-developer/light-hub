package de.randombyte.lighthub.config

object Configs {
    val general = ConfigHolder.create<GeneralConfig>(folder = "", name = "general")

    private val configs = listOf(general)

    fun reload() {
        configs.forEach { it.reload() }
    }
}