package de.randombyte.lighthub.config

object GlobalConfigs {
    val general = createConfigHolder<GeneralConfig>(folder = "", name = "general")

    private val configs = listOf(general)

    fun reload() {
        configs.forEach { it.reload() }
        checkConfigs()
    }

    private fun checkConfigs() {
        require(!general.config.run { `bpm-fader-min`..`bpm-fader-max` }.isEmpty()) {
            "[general] BPM fader min must be smaller than max!"
        }
    }
}