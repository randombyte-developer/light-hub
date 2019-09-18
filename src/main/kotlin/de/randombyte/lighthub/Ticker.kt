package de.randombyte.lighthub

import de.randombyte.lighthub.show.flows.Tickable
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.MonoClock
import kotlin.time.seconds

@ExperimentalTime
object Ticker {

    private const val SLEEP_TIME_MILLIS = 10L
    private const val TICKS_PER_SECOND = 20
    private val DURATION_PER_TICK = (1.0 / TICKS_PER_SECOND).seconds

    private var clockMark = MonoClock.markNow()
    private val tickables = mutableSetOf<Tickable>()

    fun register(tickable: Tickable) {
        tickables += tickable
    }

    fun runBlocking() {
        while (true) {
            if (clockMark.elapsedNow() > DURATION_PER_TICK) {
                tickables.forEach { it.onTick() }
                clockMark = MonoClock.markNow()
            }

            TimeUnit.MILLISECONDS.sleep(SLEEP_TIME_MILLIS)
        }
    }
}