package de.randombyte.lighthub.show.flows

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.show.flows.manualcolor.ManualDeviceControl
import de.randombyte.lighthub.show.tickables.Ticker
import kotlin.time.ExperimentalTime

@ExperimentalTime
object FlowManager {

    private val flows = mutableSetOf<Flow<*>>()

    // special handling with exclusive rights to claim Devices
    lateinit var manualDeviceControl: ManualDeviceControl

    /**
     * Adds all Devices to this Flow which it is designed to use, except the ones exclusively claimed
     * by [ManualDeviceControl]. All devices used in the [flow] are removed from the
     * other [flows] (except the exclusive ones). This ensures that no two [Flow]s try to use one device at a time.
     */
    fun requestDevices(flow: Flow<*>) {
        Ticker.register(flow) // register it in one go for convenience
        flows += flow // add operation on Set<Flow>

        // offer Devices to new Flow
        flow.usedDevices.clear()
        flow.offerDevices { !it.isExclusivelyUsedInManualControlFlow }

        // remove used Devices from other Flows
        flows
            .filter { it != flow } // don't remove Devices from itself
            .forEach { otherFlow ->
                otherFlow.usedDevices.removeAll { device -> device in flow.usedDevices }
            }

        flow.onResume()
    }

    val Device.isExclusivelyUsedInManualControlFlow get() = this in manualDeviceControl.claimedDevices
}