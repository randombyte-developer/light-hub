package de.randombyte.lighthub.osc.devices.features.colors

interface Color {
    abstract class Config(open val colors: Map<String, Color> = emptyMap()) {
        companion object {
            const val FILE_NAME = "colors"
        }
    }
}