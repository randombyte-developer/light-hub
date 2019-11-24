package de.randombyte.lighthub.show

import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.show.masterflows.MasterFlow
import de.randombyte.lighthub.utils.containsIgnoreType
import kotlin.time.ExperimentalTime

@ExperimentalTime
object MasterFlowManager {
    lateinit var active: MasterFlow<*>
    lateinit var fallbackMasterFlow: MasterFlow<*>

    fun activate(masterFlow: MasterFlow<*>) {
        // blackout all lights except the ones in the MasterFlow
        ThatShow.blackout(DevicesManager.lights.filterNot { masterFlow.devices.containsIgnoreType(it) } as List<ShutterFeature>)
        masterFlow.onActivate()
        active = masterFlow
        if (masterFlow.isFallback) fallbackMasterFlow = masterFlow
    }

    fun activateFallback() {
        activate(fallbackMasterFlow)
    }
}