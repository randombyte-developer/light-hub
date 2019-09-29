package de.randombyte.lighthub.show.tickables

import de.randombyte.lighthub.config.GlobalConfigs
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.MonoClock
import kotlin.time.seconds

@ExperimentalTime
object Ticker {
    val TICKS_PER_SECOND = GlobalConfigs.general.config.`animation-tick-frequency`

    private const val SLEEP_TIME_MILLIS = 10L
    private val DURATION_PER_TICK = (1.0 / TICKS_PER_SECOND).seconds

    private var clockMark = MonoClock.markNow()

    private var tick = 0uL
    private var beat = 0uL

    var bpm = 120
    private val ticksPerBeat get() = (TICKS_PER_SECOND * 60 / bpm).toULong()

    private val tickables = mutableSetOf<Tickable>()

    fun register(tickable: Tickable) {
        tickables += tickable
    }

    fun runBlocking() {
        while (true) {
            if (clockMark.elapsedNow() > DURATION_PER_TICK) {

                tick++
                val isOnBeat = bpm > 0 && tick.rem(ticksPerBeat) == 0uL
                if (isOnBeat) beat++

                tickables.forEach {
                    it.onTick(tick)
                    if (isOnBeat) {
                        it.onBeat(beat)
                    }
                }
                clockMark = MonoClock.markNow()
            }

            TimeUnit.MILLISECONDS.sleep(SLEEP_TIME_MILLIS)
        }
    }
}