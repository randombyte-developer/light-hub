package de.randombyte.lighthub.show.quickeffects

import de.randombyte.lighthub.show.FlowManager
import de.randombyte.lighthub.show.MasterFlowManager
import kotlin.time.ExperimentalTime

@ExperimentalTime
abstract class QuickEffect {

    open fun activate() {
        deactivateFlows()
    }

    open fun deactivate() {
        activateFallback()
    }

    private fun deactivateFlows() {
        FlowManager.removeAllDevicesFromFlows()
        FlowManager.blockFlowChanges = true
    }

    private fun activateFallback() {
        FlowManager.blockFlowChanges = false
        MasterFlowManager.activateFallback()
    }
}