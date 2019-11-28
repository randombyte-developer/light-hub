package de.randombyte.lighthub.osc.devices.features.colors

abstract class FixedColor(val id: String, val dmxRange: IntRange) : Color

abstract class FixedColorOff : FixedColor("off", -1..-1)