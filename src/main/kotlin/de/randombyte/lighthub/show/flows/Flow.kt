package de.randombyte.lighthub.show.flows

import kotlin.time.ExperimentalTime

/**
 * A [Flow] takes control over some devices. It uses the features (-interfaces) of the devices to control them. A device
 * can only be used by one Flow at a time. A Flow can use only a subset of [assignedDevices], only the ones which are
 * currently in use are in [usedDevices].
 *
 * @param assignedDevices are the devices which the Flow is designed to use
 * @param usedDevices are the devices which are currently in use, this list will vary in size during runtime, it will
 *      only contain devices which are in [assignedDevices]
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