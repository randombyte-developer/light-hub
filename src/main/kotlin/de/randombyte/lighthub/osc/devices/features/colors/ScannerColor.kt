package de.randombyte.lighthub.osc.devices.features.colors

/**
 * For the American DJ Scan 250 EX.
 */
sealed class ScannerColor(id: String, dmxRange: IntRange) : FixedColor(id, dmxRange) {

    object Off : FixedColorOff()
    object White : ScannerColor("white", 0..10)
    object Red : ScannerColor("red", 11..21)
    object Blue : ScannerColor("blue", 22..32)
    object Green : ScannerColor("green", 33..43)
    object Yellow : ScannerColor("yellow", 44..54)
    object Orange : ScannerColor("orange", 55..65)
    object Pink : ScannerColor("pink", 66..76)
    object LightBlue : ScannerColor("light-blue", 77..87)
    object Uv : ScannerColor("uv", 88..98)
    object Magenta : ScannerColor("magenta", 99..109)
    object OrangeRed : ScannerColor("orange-red", 110..120)
    object LightBlueMagenta : ScannerColor("light-blue-magenta", 121..127)
    object Rainbow : ScannerColor("rainbow", 128..255) // actually this range is a speed setting

    companion object {
        val colors = listOf(
            Off, White, Red, Blue, Green, Yellow, Orange, Pink, LightBlue, Uv, Magenta, OrangeRed, LightBlueMagenta, Rainbow
        ).map { it.id to it }.toMap()
    }

    class Config(override val colors: Map<String, ScannerColor> = emptyMap()) : Color.Config()
}