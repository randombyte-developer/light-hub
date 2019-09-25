package de.randombyte.lighthub.show.flows

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.show.tickables.Tickable
import kotlin.time.ExperimentalTime

/**
 * A [Flow] takes control over some devices. It uses the features (-interfaces) of the devices to control them. A device
 * can only be used by one Flow at a time. A Flow can only use a subset of [assignedDevices]: Only the ones which are
 * currently in use are in [usedDevices]. A Flow gets its [usedDevices] assigned from the [FlowManager].
 *
 * @param assignedDevices are the devices which the Flow is designed to use
 * @param usedDevices are the devices which are currently in use, this list will vary in size during runtime, it will
 *      only contain devices which are in [assignedDevices]
 */
@ExperimentalTime
open class Flow<T>(val assignedDevices: List<T>, val usedDevices: MutableList<T> = assignedDevices.toMutableList()) : Tickable {
    init {
        require(assignedDevices.all { it is Device }) { "List 'assignedDevices' must only contain objects of type Device!" }
    }

    open fun onResume() {}

    fun offerDevices(canBeUsed: (Device) -> Boolean) {
        usedDevices.addAll(assignedDevices.filter { canBeUsed(it as Device) })
    }
}