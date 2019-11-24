package de.randombyte.lighthub.config

object GlobalConfigs {
    val general = createConfigHolder<GeneralConfig>(folder = CONFIG_PATH, name = "general")

    fun init() {
        checkConfigs()
    }

    private fun checkConfigs() {
        require(!general.config.run { `bpm-fader-min`..`bpm-fader-max` }.isEmpty()) {
            "[general] BPM fader min must be smaller than max!"
        }
    }
}