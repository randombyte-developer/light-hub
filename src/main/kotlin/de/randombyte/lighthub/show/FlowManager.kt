package de.randombyte.lighthub.show

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.show.flows.Flow
import de.randombyte.lighthub.show.flows.colorchanger.ColorChangerFlow
import de.randombyte.lighthub.show.flows.intensity.IntensityFlow
import de.randombyte.lighthub.show.flows.pantilt.PanTiltFlow
import de.randombyte.lighthub.show.flows.rotation.RotationFlow
import de.randombyte.lighthub.utils.containsIgnoreType
import kotlin.time.ExperimentalTime

@ExperimentalTime
object FlowManager {
    val flows = setOf<Flow<*>>(ColorChangerFlow, RotationFlow, PanTiltFlow, IntensityFlow)

    val lockedDevices: List<Device> = mutableListOf()

    var blockFlowChanges = false

    fun removeAllDevicesFromFlows() {
        if (blockFlowChanges) return

        flows.forEach { it.usedDevices.clear() }
    }

    fun addDevicesToFlow(flow: Flow<*>, devices: List<Any>) {
        if (blockFlowChanges) return

        devices.forEach { device ->
            require(device in flow.acceptedDevices) {
                "Only Devices which the Flow '${flow::class.simpleName}' is designed to use are accepted! Given Device: '${device::class.simpleName}'."
            }
            if (!lockedDevices.containsIgnoreType(device)) {
                // This is hacky but it will definitely work because the previous check would catch all given devices
                // which were not designed to be used with this Flow. The type checking was done via generics in the Flow
                // class.
                (flow.usedDevices as MutableList<Any>) += device
            }
        }

        flow.onActivate()
    }

    fun lockDevice(device: Device) {
        (lockedDevices as MutableList) += device
        flows.forEach { it.usedDevices.remove(device) }
    }

    fun unlockDevice(device: Device) {
        (lockedDevices as MutableList) -= device
    }
}