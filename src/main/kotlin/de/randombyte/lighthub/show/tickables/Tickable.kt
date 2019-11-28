package de.randombyte.lighthub.show.tickables

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.show.flows.AutoPatternsConfig
import de.randombyte.lighthub.show.tickables.Ticker.ticksPerBeat
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface Tickable {
    fun onActivate() {}
    fun onTick(tick: ULong) {}
    fun onBeat(beat: ULong) {} // todo: pretty much unused, integrate this again with the ticks and "next change"

    fun getTicksUntilNextChange(currentTick: ULong, device: Device, config: AutoPatternsConfig): Int {
        with(config) {
            val specificDeviceOffset = `device-type-offset` * device.number * ticksPerBeat
            val globalOffset = `global-type-offset` * ticksPerBeat
            val ticksSinceLastChange = ((currentTick + specificDeviceOffset.toULong() + globalOffset.toULong())
                .rem((interval * ticksPerBeat).toULong())).toInt()
            val ticksUntilNextChange = (interval * ticksPerBeat) - ticksSinceLastChange
            return ticksUntilNextChange
        }
    }
}