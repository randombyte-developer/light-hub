package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.ConfigHolder.Companion.create
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.devices.features.MasterDimmerFeatureImpl
import de.randombyte.lighthub.osc.devices.features.RgbwFeatureImpl
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

class TsssPar(number: Int, dmxAddress: Int) : Device(
    type = Companion,
    number = number,
    dmxAddress = dmxAddress
), RgbwFeatureImpl, MasterDimmerFeatureImpl {

    companion object : Type<TsssPar> {
        override val clazz = TsssPar::class
        override val constructor = ::TsssPar
        override val id = "tsss-par"
        override val channels = 8

        override val metaConfigHolder = create<MetaConfig>(id, "meta")
    }

    private val oscMode = createOscChannel("mode", 0)
    private val oscColorMacro = createOscChannel("color-macro", 1)
    private val oscSpeed = createOscChannel("speed", 2)
    override val oscMasterDimmer = createOscChannel("master-dimmer", 3)
    override val oscRed = createOscChannel("red", 4)
    override val oscGreen = createOscChannel("green", 5)
    override val oscBlue = createOscChannel("blue", 6)
    override val oscWhite = createOscChannel("white", 7)

    override val oscChannelList = OscChannelList(
        oscMode,
        oscColorMacro,
        oscSpeed,
        oscMasterDimmer,
        oscRed,
        oscGreen,
        oscBlue,
        oscWhite
    )

    fun dimmingMode() {
        oscMode.sendValue(0)
        oscMasterDimmer.sendValue(DMX_RANGE.last)
    }

    fun blackout() {
        oscMode.sendValue(0)
    }
}