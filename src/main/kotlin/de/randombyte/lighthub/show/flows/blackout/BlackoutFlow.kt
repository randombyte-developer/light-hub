package de.randombyte.lighthub.show.flows.blackout

import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.show.flows.Flow
import kotlin.time.ExperimentalTime

@ExperimentalTime
class BlackoutFlow(assignedDevices: List<ShutterFeature>) : Flow<ShutterFeature>(assignedDevices) {
    override fun onResume() {
        usedDevices.forEach { it.noLight() }
    }
}