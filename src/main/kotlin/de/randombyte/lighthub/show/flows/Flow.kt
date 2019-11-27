package de.randombyte.lighthub.show.flows

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.show.tickables.Tickable
import de.randombyte.lighthub.show.tickables.Ticker
import kotlin.time.ExperimentalTime

/**
 * A [Flow] takes control over some devices. It uses the features (-interfaces) of the devices to control them.
 * A Flow can only use a subset of [acceptedDevices]: Only the ones which are  currently in use are in [usedDevices].
 * A Flow gets its [usedDevices] assigned by the [FlowManager].
 *
 * @param acceptedDevices are the devices which the Flow is designed to use
 * @param usedDevices are the devices which are currently in use, this list will vary in size during runtime, it will
 *      only contain devices which are also in acceptedDevices
 */
@ExperimentalTime
open class Flow<T>(val acceptedDevices: List<T>, val usedDevices: MutableList<T> = acceptedDevices.toMutableList()) : Tickable {

    init {
        require(acceptedDevices.all { it is Device }) { "List 'acceptedDevices' must only contain objects of type Device!" }
    }

    override fun onActivate() {
        usedDevices.forEach { onActivate(it) }
    }

    override fun onTick(tick: ULong) {
        usedDevices.forEach { onTick(tick, it) }
    }

    override fun onBeat(beat: ULong) {
        usedDevices.forEach { onBeat(beat, it) }
    }

    open fun onActivate(device: T) {}
    open fun onTick(tick: ULong, device: T) {}
    open fun onBeat(beat: ULong, device: T) {}

    inline fun <reified C : AutoPatternsConfig> getTicksUntilNextChange(tick: ULong, device: Device) =
        getTicksUntilNextChange(tick, device, device.type.getCurrentMasterFlowConfig<C>().config)

    inline fun <reified C : AutoPatternsConfig> isOnChange(tick: ULong, device: Device) =
        getTicksUntilNextChange<C>(tick, device) == 1

    inline fun <reified C : AutoPatternsConfig> getIntervalTicks(device: Device) =
        device.type.getCurrentMasterFlowConfig<C>().config.interval * Ticker.ticksPerBeat

    inline fun <reified C : AutoPatternsConfig> getPercentUntilNextChange(tick: ULong, device: Device): Double {
        return getTicksUntilNextChange<C>(tick, device).toDouble() / getIntervalTicks<C>(device)
    }
}