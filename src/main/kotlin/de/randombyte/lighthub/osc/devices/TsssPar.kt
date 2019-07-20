package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.ConfigHolder.Companion.create
import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.osc.devices.features.Feature
import de.randombyte.lighthub.osc.devices.features.MetaConfig
import de.randombyte.lighthub.osc.devices.features.colors.RgbwFeature
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

class TsssPar(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
) {

    companion object : Type<TsssPar> {
        override val clazz = TsssPar::class
        override val constructor = ::TsssPar
        override val id = "tsss-par"
        override val channels = 8

        override val metaConfigHolder = create<MetaConfig>(id, "meta")
        val colors = create<RgbwFeature.RgbwConfig>(id, "colors")

        override val configHolders = listOf(colors)
    }

    private val oscMode = "mode".toOscChannel()
    private val oscColorMacro = "color-macro".toOscChannel()
    private val oscSpeed = "speed".toOscChannel()
    private val oscMasterDimmer = "master-dimmer".toOscChannel()
    private val oscRed = "red".toOscChannel()
    private val oscGreen = "green".toOscChannel()
    private val oscBlue = "blue".toOscChannel()
    private val oscWhite = "white".toOscChannel()

    override val oscChannelMapping = OscChannelMapping(
        0 to oscMode,
        1 to oscColorMacro,
        2 to oscSpeed,
        3 to oscMasterDimmer,
        4 to oscRed,
        5 to oscGreen,
        6 to oscBlue,
        7 to oscWhite
    )

    val rgbwFeature = RgbwFeature(Companion, oscRed, oscGreen, oscBlue, oscWhite)

    override val features: List<Feature> = listOf(rgbwFeature)

    fun dimmingMode() {
        oscMode.sendValue(0)
        oscMasterDimmer.sendValue(DMX_RANGE.last)
    }

    fun blackout() {
        oscMode.sendValue(0)
    }
}