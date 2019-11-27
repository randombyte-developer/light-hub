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

    protected fun deactivateFlows() {
        FlowManager.removeAllDevicesFromFlows()
        FlowManager.blockFlowChanges = true
    }

    protected fun activateFallback() {
        FlowManager.blockFlowChanges = false
        MasterFlowManager.activateFallback()
    }
}