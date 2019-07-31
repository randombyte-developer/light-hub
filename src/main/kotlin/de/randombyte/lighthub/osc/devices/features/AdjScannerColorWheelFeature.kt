package de.randombyte.lighthub.osc.devices.features

/**
 * This feature is for the specific American DJ Scan 250 EX.
 */
interface AdjScannerColorWheelFeature : ConfigurableFeature {

    var color: Color

    enum class Color(val dmxValue: Int) {
        White(0),
        Red(11),
        DeepBlue(22),
    }
}