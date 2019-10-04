package de.randombyte.lighthub.show.flows

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.show.flows.manualcolor.ManualDeviceControl
import kotlin.time.ExperimentalTime

@ExperimentalTime
object FlowManager {

    private val flows = mutableSetOf<Flow<*>>()

    private val independentFlows = mutableSetOf<Flow<*>>()

    // when a Device is claimed, it can only be used by ManualDeviceControl
    var claimedDevices = emptyList<Device>()
        private set

    /**
     * An independent [Flow] doesn't interfere with any other Flows. The [Flow.usedDevices] usually don't ever change.
     * Any changes this Flow does to [Device]s is done in this Flow only. An independent Flow just keeps its [Flow.usedDevices],
     * except Devices get claimed by manual control.
     */
    fun registerIndependentFlow(flow: Flow<*>) {
        independentFlows += flow
    }

    /**
     * Adds all Devices to this Flow which it is designed to use, except the ones exclusively claimed
     * by [ManualDeviceControl]. All devices used in the [flow] are removed from the
     * other [flows] (except the exclusive ones). This ensures that no two [Flow]s try to use one device at a time.
     */
    fun requestDevices(flow: Flow<*>) {
        flows += flow // add operation on Set<Flow>

        flow.usedDevices.clear()

        // offer Devices to new Flow, if not claimed by manual control
        flow.offerDevices { !it.isClaimed }

        if (!flow.isIndependent) {
            // remove used Devices from other Flows
            flows
                .filter { it != flow && !it.isIndependent } // don't remove Devices from itself and independent Flows
                .forEach { otherFlow ->
                    otherFlow.usedDevices.removeAll { device -> device in flow.usedDevices }
                }
        }

        flow.onResume()
    }

    fun claimDevice(device: Device) {
        claimedDevices += device
        flows.forEach {
            it.usedDevices.remove(device)
        }
    }

    fun freeDevice(device: Device) {
        claimedDevices -= device
    }

    private val Flow<*>.isIndependent get() = this in independentFlows
}

@ExperimentalTime
val Device.isClaimed get() = this in FlowManager.claimedDevices