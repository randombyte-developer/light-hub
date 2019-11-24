package de.randombyte.lighthub.show.tickables

import de.randombyte.lighthub.config.GlobalConfigs
import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.show.flows.AutoPatternsConfig
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface Tickable {
    fun onActivate() {}
    fun onTick(tick: ULong) {}
    fun onBeat(beat: ULong) {}

    fun getTicksUntilNextChange(currentTick: ULong, device: Device, config: AutoPatternsConfig): Int {
        with(config) {
            val ticksPerBeat = GlobalConfigs.general.config.`ticks-per-beat`
            val specificDeviceOffset = `device-type-offset` * device.number * ticksPerBeat
            val globalOffset = `global-type-offset` * ticksPerBeat
            val ticksElapsedSinceLastChange = ((currentTick + specificDeviceOffset.toULong() + globalOffset.toULong())
                .rem((interval * ticksPerBeat).toULong())).toInt()
            val ticksUntilNextChange = (interval * ticksPerBeat) - ticksElapsedSinceLastChange
            return ticksUntilNextChange
        }
    }
}