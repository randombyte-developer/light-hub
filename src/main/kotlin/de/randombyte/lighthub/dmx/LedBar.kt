package de.randombyte.lighthub.dmx

import de.randombyte.lighthub.config.Color
import de.randombyte.lighthub.config.Color.Rgb
import de.randombyte.lighthub.config.Color.Rgb.Companion.new
import de.randombyte.lighthub.config.loader.toConfigHolder

class LedBar(oscBasePath: String, startAddress: Int) : Light<Rgb>(oscBasePath, startAddress) {

    class Config(
        meta: Meta = Meta(
            manufacturer = "Stairville",
            model = "LED BAR 252 RGB (no. 234564)",
            mode = "Mode 1",
            name = "LedBar"
        ),
        addresses: List<UByte> = listOf(1u), // todo
        colors: Map<String, Rgb> = mapOf(
            "red" to new(r = 255, g = 0, b = 0),
            "green" to new(r = 0, g = 255, b = 0),
            "blue" to new(r = 0, g = 0, b = 255)
        )
    ) : Light.Config<Rgb>(meta, addresses, colors)

    companion object : Type<Rgb> {
        override val configHolder = "led-bar.conf".toConfigHolder<Config>()
        override val channels = 11
    }

    private val dmxMode = DmxChannel("$oscBasePath/mode")
    private val dmxRed = DmxChannel("$oscBasePath/red")
    private val dmxGreen = DmxChannel("$oscBasePath/green")
    private val dmxBlue = DmxChannel("$oscBasePath/blue")

    override var color: Rgb = configHolder.config.colors.getValue(Color.DEFAULT_COLOR_KEY)
        set(value) {
            field = value
            dmxRed.sendValue(value.red)
            dmxGreen.sendValue(value.green)
            dmxBlue.sendValue(value.blue)
        }

    override fun blackout() {
        dmxMode.sendValue(0)
    }
}