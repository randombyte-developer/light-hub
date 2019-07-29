package de.randombyte.lighthub.osc.devices.features.impl

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.features.StrobeFeature

class StrobeFeatureImpl(
    val oscStrobeMode: OscChannel,
    val oscStrobeModeValue: Int,
    val oscStrobeSpeed: OscChannel,
    val oscStrobeSpeedRange: IntRange
) : StrobeFeature {
    override fun on() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun off() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var speed: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

}