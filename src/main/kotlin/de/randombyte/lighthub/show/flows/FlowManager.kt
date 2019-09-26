package de.randombyte.lighthub.show.flows

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.show.flows.manualcolor.ManualDeviceControl
import kotlin.time.ExperimentalTime

@ExperimentalTime
object FlowManager {

    private val flows = mutableSetOf<Flow<*>>()

    // when a Device is claimed, it can only be used by ManualDeviceControl
    var claimedDevices = emptyList<Device>()
        private set

    /**
     * Adds all Devices to this Flow which it is designed to use, except the ones exclusively claimed
     * by [ManualDeviceControl]. All devices used in the [flow] are removed from the
     * other [flows] (except the exclusive ones). This ensures that no two [Flow]s try to use one device at a time.
     */
    fun requestDevices(flow: Flow<*>) {
        flows += flow // add operation on Set<Flow>

        // offer Devices to new Flow
        flow.usedDevices.clear()
        flow.offerDevices { !it.isClaimed }

        // remove used Devices from other Flows
        flows
            .filter { it != flow } // don't remove Devices from itself
            .forEach { otherFlow ->
                otherFlow.usedDevices.removeAll { device -> device in flow.usedDevices }
            }

        flow.onResume()
    }

    fun claimDevice(device: Device) {
        claimedDevices += device
        flows.forEach { it.usedDevices.remove(device) }
    }

    fun freeDevice(device: Device) {
        claimedDevices -= device
    }
}

@ExperimentalTime
val Device.isClaimed get() = this in FlowManager.claimedDevices