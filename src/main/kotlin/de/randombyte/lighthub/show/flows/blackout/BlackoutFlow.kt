package de.randombyte.lighthub.show.flows.blackout

import de.randombyte.lighthub.osc.devices.features.MasterDimmerFeature
import de.randombyte.lighthub.show.flows.Flow
import kotlin.time.ExperimentalTime

@ExperimentalTime
class BlackoutFlow(assignedDevices: List<MasterDimmerFeature>) : Flow<MasterDimmerFeature>(assignedDevices) {
    override fun onResume() {
        usedDevices.forEach { it.noLight() }
    }
}