package de.randombyte.lighthub.osc.dmx

import de.randombyte.lighthub.config.Color
import de.randombyte.lighthub.config.Color.Rgb
import de.randombyte.lighthub.config.Color.Rgb.Companion.new
import de.randombyte.lighthub.config.loader.toConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.OscChannelMapping
import kotlin.reflect.KClass

class LedBar(number: Int, startAddress: Int) : Light<Rgb>(
    type = Companion,
    oscBasePath = "LedBar",
    number = number,
    startAddress = startAddress
) {

    class Config(
        meta: Meta = Meta(
            manufacturer = "Stairville",
            model = "LED BAR 252 RGB (no. 234564)",
            mode = "Mode 1",
            name = "LedBar"
        ),
        addresses: List<Int> = emptyList(),
        colors: Map<String, Rgb> = mapOf(
            "Red" to new(r = 255, g = 0, b = 0),
            "Green" to new(r = 0, g = 255, b = 0),
            "Blue" to new(r = 0, g = 0, b = 255)
        )
    ) : Light.Config<Rgb>(meta, addresses, colors)

    companion object : Type<Rgb> {
        override val configHolder = "led-bar.conf".toConfigHolder<Config>()
        override val channels = 11
        override val colorClass = Rgb::class
    }

    private val oscMode = "Mode".toOscChannel()
    private val oscRed = "Red".toOscChannel()
    private val oscGreen = "Green".toOscChannel()
    private val oscBlue = "Blue".toOscChannel()

    override val oscChannelMapping = OscChannelMapping(mapOf(
        0 to oscMode,
        1 to oscRed,
        2 to oscGreen,
        3 to oscBlue
    ))

    override var color: Rgb = configHolder.config.colors.getValue(Color.DEFAULT_COLOR_KEY)
        set(value) {
            field = value
            oscRed.sendValue(value.red)
            oscGreen.sendValue(value.green)
            oscBlue.sendValue(value.blue)
        }

    override fun blackout() {
        oscMode.sendValue(0)
    }
}