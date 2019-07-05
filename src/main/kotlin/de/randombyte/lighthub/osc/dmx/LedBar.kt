package de.randombyte.lighthub.osc.dmx

import de.randombyte.lighthub.config.Color
import de.randombyte.lighthub.config.Color.Rgb
import de.randombyte.lighthub.config.Color.Rgb.Companion.new
import de.randombyte.lighthub.config.loader.toConfigHolder
import de.randombyte.lighthub.osc.OscChannelMapping

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
            name = "LedBar",
            `short-name` = "LedBar"
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
    private val oscShutter = "Shutter".toOscChannel()

    private val oscRed1 = "Red1".toOscChannel()
    private val oscGreen1 = "Green1".toOscChannel()
    private val oscBlue1 = "Blue1".toOscChannel()
    private val oscRed2 = "Red2".toOscChannel()
    private val oscGreen2 = "Green2".toOscChannel()
    private val oscBlue2 = "Blue2".toOscChannel()
    private val oscRed3 = "Red3".toOscChannel()
    private val oscGreen3 = "Green3".toOscChannel()
    private val oscBlue3 = "Blue3".toOscChannel()

    private val oscReds = listOf(oscRed1, oscRed2, oscRed3)
    private val oscGreens = listOf(oscGreen1, oscGreen2, oscGreen3)
    private val oscBlues = listOf(oscBlue1, oscBlue2, oscBlue3)

    override val oscChannelMapping = OscChannelMapping(mapOf(
        0 to oscMode,
        1 to oscShutter,
        2 to oscRed1,
        3 to oscGreen1,
        4 to oscBlue1,
        5 to oscRed2,
        6 to oscGreen2,
        7 to oscBlue2,
        8 to oscRed3,
        9 to oscGreen3,
        10 to oscBlue3
    ))

    fun ledOn() {
        oscMode.sendValue(41)
        oscShutter.sendValue(0)
    }

    fun strobe() {
        ledOn()
        oscShutter.sendValue(200)
    }

    override var color: Rgb = configHolder.config.colors.getValue(Color.DEFAULT_COLOR_KEY)
        set(value) {
            field = value
            oscReds.forEach { it.sendValue(value.red) }
            oscGreens.forEach { it.sendValue(value.green) }
            oscBlues.forEach { it.sendValue(value.blue) }
        }

    override fun blackout() {
        oscMode.sendValue(0)
    }
}