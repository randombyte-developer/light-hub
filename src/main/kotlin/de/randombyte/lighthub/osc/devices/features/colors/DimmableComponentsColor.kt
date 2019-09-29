package de.randombyte.lighthub.osc.devices.features.colors

import kotlin.reflect.KProperty0

interface DimmableComponentsColor : Color {
    fun transformComponents(other: DimmableComponentsColor, transformer: (current: Int, other: Int) -> Int): Color

    val components: List<KProperty0<Int>>

    abstract class Config(override val colors: Map<String, DimmableComponentsColor> = emptyMap()) : Color.Config()
}