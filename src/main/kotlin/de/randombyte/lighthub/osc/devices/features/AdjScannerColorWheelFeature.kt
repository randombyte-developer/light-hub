package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.AdjScannerColorWheelFeature.*

/**
 * This feature is specifically for the American DJ Scan 250 EX.
 */
interface AdjScannerColorWheelFeature : Feature {

    var color: Color

    enum class Color(val dmxRange: IntRange) {
        White(0..10),
        Red(11..21),
        DeepBlue(22..32),
        Green(33..43),
        Yellow(44..54),
        Orange(55..65),
        Pink(66..76),
        LightBlue(77..87),
        Uv(88..98),
        Magenta(99..109),
        OrangeRed(110..120),
        LightBlueMagenta(121..127),
        Rainbow(128..255);
    }
}

interface AdjScannerColorWheelFeatureImpl : AdjScannerColorWheelFeature {
    val oscColorWheel: OscChannel

    override var color: Color
        get() = Color.values().first { oscColorWheel.lastValue in it.dmxRange }
        set(color) {
            oscColorWheel.sendValue(color.dmxRange.first)
        }
}