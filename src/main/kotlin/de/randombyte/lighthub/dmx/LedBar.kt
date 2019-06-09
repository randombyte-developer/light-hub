package de.randombyte.lighthub.dmx

import de.randombyte.lighthub.config.Color
import de.randombyte.lighthub.config.Color.Rgb
import de.randombyte.lighthub.config.Color.Rgb.Companion.new
import de.randombyte.lighthub.config.loader.ConfigManager
import de.randombyte.lighthub.config.loader.toConfigHolder
import de.randombyte.lighthub.config.loader.toConfigLoader
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

class LedBar(oscBasePath: String, startAddress: Int) : Light<Rgb>(oscBasePath, startAddress) {

    @ConfigSerializable class Config(
        meta: Meta = Meta(
            manufacturer = "Stairville",
            model = "LED BAR 252 RGB (no. 234564)",
            mode = "Mode 1",
            name = "LedBar"
        ),
        addresses: List<UByte> = listOf(1u), // todo
        colors: Map<String, Rgb> = mapOf(
            "red" to new(r = 255u, g = 0u, b = 0u),
            "green" to new(r = 0u, g = 255u, b = 0u),
            "blue" to new(r = 0u, g = 0u, b = 255u)
        )
    ) : Light.Config<Rgb>(meta, addresses, colors)

    companion object : Type<Rgb> {
        override val config = ConfigManager("led-bar.conf".toConfigLoader(), Config::class.java).toConfigHolder()
        override val channels = 11
        override val colors =  emptyMap<String, Rgb>() //config.get().colors
    }

    private val dmxMode = DmxChannel("$oscBasePath/mode")
    private val dmxRed = DmxChannel("$oscBasePath/red")
    private val dmxGreen = DmxChannel("$oscBasePath/green")
    private val dmxBlue = DmxChannel("$oscBasePath/blue")

    var color: Rgb = config.get().colors.getValue(Color.DEFAULT_COLOR_KEY)
        set(value) {
            field = value
            dmxRed.sendValue(value.red)
            dmxGreen.sendValue(value.green)
            dmxBlue.sendValue(value.blue)
        }

    override fun blackout() {
        dmxMode.sendValue(0u)
    }
}