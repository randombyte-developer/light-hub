package de.randombyte.lighthub.show.tickables

import de.randombyte.lighthub.config.GlobalConfigs
import kotlin.time.ExperimentalTime
import kotlin.time.MonoClock
import kotlin.time.seconds

@ExperimentalTime
object Ticker {
    val TICKS_PER_SECOND = GlobalConfigs.general.config.`ticks-per-second`

    private val DURATION_PER_TICK = (1.0 / TICKS_PER_SECOND).seconds

    private var clockMark = MonoClock.markNow()

    private var tick = 0uL
    private var beat = 0uL

    var bpm = GlobalConfigs.general.config.`beats-per-minute`
    val ticksPerBeat get() = (TICKS_PER_SECOND * 60 / bpm)

    private val tickables = mutableSetOf<Tickable>()

    fun register(tickable: Tickable) {
        tickables += tickable
    }

    fun runBlocking() {
        while (true) {
            if (clockMark.elapsedNow() > DURATION_PER_TICK) {
                clockMark = MonoClock.markNow()

                tick++
                val isOnBeat = bpm > 0 && tick.rem(ticksPerBeat.toULong()) == 0uL
                if (isOnBeat) beat++

                tickables.forEach {
                    it.onTick(tick)
                    if (isOnBeat) {
                        it.onBeat(beat)
                    }
                }
            }
        }
    }
}