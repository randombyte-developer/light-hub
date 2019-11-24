package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.createConfigHolder
import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.createOscChannel
import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.osc.devices.features.RotationFeature.RotationSpeedsConfig
import de.randombyte.lighthub.osc.devices.features.StrobeFeature.StrobeSpeedsConfig
import de.randombyte.lighthub.osc.devices.features.colors.QuadPhaseColor
import de.randombyte.lighthub.show.flows.colorchanger.ColorSetsConfig
import kotlin.time.ExperimentalTime

@ExperimentalTime
class QuadPhase(number: Int, dmxAddress: Int) : Device(
        type = Companion,
        number = number,
        dmxAddress = dmxAddress
), FixedColorFeatureImpl, MasterDimmerFeatureImpl, StrobeFeatureImpl, RotationFeatureImpl {

    @ExperimentalTime
    companion object : Type<QuadPhase>(), ColorFeature.Config, StrobeFeature.Config, RotationFeature.Config {
        override val clazz = QuadPhase::class
        override val constructor = ::QuadPhase
        override val id = "quad-phase"
        override val channelsCount = 4

        override val metaConfig = createConfigHolder<MetaConfig>(MetaConfig.FILE_NAME)
        override val colorSetsConfig = createConfigHolder<ColorSetsConfig>(ColorSetsConfig.FILE_NAME)
        override val strobeSpeeds = createConfigHolder<StrobeSpeedsConfig>(StrobeSpeedsConfig.FILE_NAME)
        override val rotationSpeeds = createConfigHolder<RotationSpeedsConfig>(RotationSpeedsConfig.FILE_NAME)
        override val configs = listOf(colorSetsConfig, strobeSpeeds, rotationSpeeds)
    }

    override val oscColorSelection = createOscChannel("color-selection", 0)
    override val oscRotation = createOscChannel("rotation", 1)
    override val oscStrobeSpeed = createOscChannel("strobe-speed", 2)
    override val oscMasterDimmer = createOscChannel("shutter", 3)

    override val oscSpeedRange = 1..255
    override val oscNoStrobe = 0

    override val colors = QuadPhaseColor.colors

    override fun noLight() {
        super.noLight()
        rotationSpeed = 0 // don't have to rotate when you can't even see it
    }

    override val oscChannelList = OscChannelList(
        oscColorSelection,
        oscRotation,
        oscStrobeSpeed,
        oscMasterDimmer
    )
}