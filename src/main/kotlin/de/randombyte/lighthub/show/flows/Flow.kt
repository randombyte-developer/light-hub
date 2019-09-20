package de.randombyte.lighthub.show.flows

import kotlin.time.ExperimentalTime

/**
 * @param assignedDevices are the devices which the Flow is designed to use
 * @param usedDevices are the devices which are currently in use, this list will shrink when activating other Flows,
 *  and will be a copy of [assignedDevices] again when activating this Flow
 */
@ExperimentalTime
open class Flow<T>(val assignedDevices: List<T>, val usedDevices: MutableList<T> = assignedDevices.toMutableList()) {

    open fun onResume() {}
    open fun onTick() {}

    fun useAllDevices() {
        usedDevices.clear()
        usedDevices.addAll(assignedDevices)
    }
}