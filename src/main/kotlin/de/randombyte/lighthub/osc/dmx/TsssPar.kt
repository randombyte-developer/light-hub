package de.randombyte.lighthub.osc.dmx

import de.randombyte.lighthub.config.Color
import de.randombyte.lighthub.config.Color.Rgb
import de.randombyte.lighthub.config.Color.Rgb.Companion.new
import de.randombyte.lighthub.config.Color.Rgbw
import de.randombyte.lighthub.config.Color.Rgbw.Companion.new
import de.randombyte.lighthub.config.loader.toConfigHolder
import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE
import kotlin.reflect.KClass

class TsssPar(number: Int, startAddress: Int) : Light<Rgbw>(
    type = Companion,
    oscBasePath = "TsssPar",
    number = number,
    startAddress = startAddress
) {

    class Config(
        meta: Meta = Meta(
            manufacturer = "TSSS",
            model = "TSSS",
            mode = "Normal",
            name = "TSSS"
        ),
        addresses: List<Int> = emptyList(),
        colors: Map<String, Rgbw> = mapOf(
            "Red" to new(r = 255, g = 0, b = 0, w = 0),
            "Green" to new(r = 0, g = 255, b = 0, w = 0),
            "Blue" to new(r = 0, g = 0, b = 255, w = 0)
        )
    ) : Light.Config<Rgbw>(meta, addresses, colors)

    companion object : Type<Rgbw> {
        override val configHolder = "tsss-par.conf".toConfigHolder<Config>()
        override val channels = 8
        override val colorClass = Rgbw::class
    }

    private val oscMode = "Mode".toOscChannel()
    private val oscColorMacro = "ColorMacro".toOscChannel()
    private val oscSpeed = "Speed".toOscChannel()
    private val oscMasterDimmer = "MasterDimmer".toOscChannel()
    private val oscRed = "Red".toOscChannel()
    private val oscGreen = "Green".toOscChannel()
    private val oscBlue = "Blue".toOscChannel()
    private val oscWhite = "White".toOscChannel()

    override val oscChannelMapping = OscChannelMapping(mapOf(
        0 to oscMode,
        1 to oscColorMacro,
        2 to oscSpeed,
        3 to oscMasterDimmer,
        4 to oscRed,
        5 to oscGreen,
        6 to oscBlue,
        7 to oscWhite
    ))

    fun dimmingMode() {
        oscMode.sendValue(0)
        oscMasterDimmer.sendValue(DMX_RANGE.endInclusive)
    }

    override var color: Rgbw = configHolder.config.colors.getValue(Color.DEFAULT_COLOR_KEY)
        set(value) {
            field = value
            oscRed.sendValue(value.red)
            oscGreen.sendValue(value.green)
            oscBlue.sendValue(value.blue)
            oscWhite.sendValue(value.white)
        }

    override fun blackout() {
        oscMode.sendValue(0)
    }
}