package de.randombyte.lighthub.osc.devices.features.colors

interface DimmableComponentsColor : Color {
    fun transformComponents(other: DimmableComponentsColor, transformer: (current: Int, other: Int) -> Int): Color

    abstract class Config(override val colors: Map<String, DimmableComponentsColor> = emptyMap()) : Color.Config()
}