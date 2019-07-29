package de.randombyte.lighthub.osc.devices.features.impl

import de.randombyte.lighthub.osc.OscChannel
import de.randombyte.lighthub.osc.devices.HexPar
import de.randombyte.lighthub.osc.devices.features.MasterDimmerFeature
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE
import kotlin.reflect.KProperty

class MasterDimmerFeatureImpl(
    val oscMasterDimmer: OscChannel
) : MasterDimmerFeature {
    override var masterDimmer: Int
        get() = oscMasterDimmer.lastValue
        set(value) { oscMasterDimmer.sendValue(value) }

    fun on() { masterDimmer = DMX_RANGE.last }
    fun off() { masterDimmer = DMX_RANGE.first }
}