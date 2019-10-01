package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.osc.OscChannel

interface MasterDimmerFeature : ShutterFeature {
    var masterDimmer: Int
}

interface MasterDimmerFeatureImpl : MasterDimmerFeature, ShutterFeatureImpl {
    val oscMasterDimmer: OscChannel
    override val oscShutter get() = oscMasterDimmer // to preserve the ShutterFeature functions

    override var masterDimmer: Int
        get() = oscMasterDimmer.lastValue
        set(value) { oscMasterDimmer.sendValue(value) }
}