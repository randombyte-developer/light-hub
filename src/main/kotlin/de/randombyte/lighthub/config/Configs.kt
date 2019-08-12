package de.randombyte.lighthub.config

object Configs {
    val general = createConfigHolder<GeneralConfig>(folder = "", name = "general")

    private val configs = listOf(general)

    fun reload() {
        configs.forEach { it.reload() }
    }
}