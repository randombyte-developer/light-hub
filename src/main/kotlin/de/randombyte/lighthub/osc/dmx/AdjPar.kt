package de.randombyte.lighthub.osc.dmx

import de.randombyte.lighthub.config.Color
import de.randombyte.lighthub.config.Color.Rgbwauv
import de.randombyte.lighthub.config.Color.Rgbwauv.Companion.new
import de.randombyte.lighthub.config.loader.toConfigHolder
import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

class AdjPar(number: Int, startAddress: Int) : Light<Rgbwauv>(
    type = Companion,
    oscBasePath = "AdjPar",
    number = number,
    startAddress = startAddress
) {

    class Config(
        colors: Map<String, Rgbwauv> = mapOf(
            "Red" to new(r = 255, g = 0, b = 0, w = 0, a = 0, uv = 0),
            "Green" to new(r = 0, g = 255, b = 0, w = 0, a = 0, uv = 0),
            "Blue" to new(r = 0, g = 0, b = 255, w = 0, a = 0, uv = 0)
        ),
        addresses: List<Int> = emptyList(), // todo
        meta: Meta = Meta(
            manufacturer = "American DJ",
            model = "Mega Hex Par",
            mode = "12 channel",
            name = "ADJPar",
            `short-name` = "ADJ"
        )
    ) : Light.Config<Rgbwauv>(meta, addresses, colors)

    companion object : Type<Rgbwauv> {
        override val configHolder = "adj-par.conf".toConfigHolder<Config>()
        override val channels = 12
        override val colorClass = Rgbwauv::class
    }

    private val oscRed = "Red".toOscChannel()
    private val oscGreen = "Green".toOscChannel()
    private val oscBlue = "Blue".toOscChannel()
    private val oscWhite = "White".toOscChannel()
    private val oscAmber = "Amber".toOscChannel()
    private val oscUv = "Uv".toOscChannel()
    private val oscMasterDimmer = "MasterDimmer".toOscChannel()
    private val oscShutter = "Shutter".toOscChannel()
    private val oscProgram = "ProgrammSelection".toOscChannel()
    private val oscMacro = "Macros".toOscChannel()
    private val oscSpeed = "Speed".toOscChannel()
    private val oscDimmerCurve = "DimmerCurve".toOscChannel()

    override val oscChannelMapping = OscChannelMapping(mapOf(
        0 to oscRed,
        1 to oscGreen,
        2 to oscBlue,
        3 to oscWhite,
        4 to oscAmber,
        5 to oscUv,
        6 to oscMasterDimmer,
        7 to oscShutter,
        8 to oscProgram,
        9 to oscMacro,
        10 to oscSpeed,
        11 to oscDimmerCurve
    ))

    fun ledOn() {
        oscShutter.sendValue(32)
    }

    fun dimmingMode() {
        oscProgram.sendValue(0)
        ledOn()
        masterDimmer = DMX_RANGE.endInclusive
    }

    var masterDimmer: Int = DMX_RANGE.endInclusive
        set(value) {
            field = oscMasterDimmer.sendValue(value)
        }

    override var color: Rgbwauv = AdjPar.configHolder.config.colors.getValue(Color.DEFAULT_COLOR_KEY)
        set(value) {
            field = value
            oscRed.sendValue(value.red)
            oscGreen.sendValue(value.green)
            oscBlue.sendValue(value.blue)
            oscWhite.sendValue(value.white)
            oscAmber.sendValue(value.amber)
            oscUv.sendValue(value.uv)
        }

    override fun blackout() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}