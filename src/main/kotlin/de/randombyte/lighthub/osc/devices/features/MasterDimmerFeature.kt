package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE

interface MasterDimmerFeature : Feature {
    var masterDimmer: Int

    fun fullIntensity() { masterDimmer = DMX_RANGE.last }
    fun noLight() { masterDimmer = DMX_RANGE.first }
}

interface MasterDimmerFeatureImpl : MasterDimmerFeature {
    val oscMasterDimmer: OscChannel

    override var masterDimmer: Int
        get() = oscMasterDimmer.lastValue
        set(value) { oscMasterDimmer.sendValue(value) }
}