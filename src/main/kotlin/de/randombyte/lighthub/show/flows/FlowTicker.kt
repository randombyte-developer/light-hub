package de.randombyte.lighthub.show.flows

import de.randombyte.lighthub.config.GlobalConfigs
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.MonoClock
import kotlin.time.seconds

@ExperimentalTime
object FlowTicker {

    val TICKS_PER_SECOND = GlobalConfigs.general.config.`animation-tick-frequency`

    private const val SLEEP_TIME_MILLIS = 10L
    private val DURATION_PER_TICK = (1.0 / TICKS_PER_SECOND).seconds

    private var clockMark = MonoClock.markNow()
    private val flows = mutableSetOf<Flow<*>>()

    /**
     * Adds the [activatingFlow] to the running/ticking flows. Adds all devices to this Flow which it is
     * designed to use. All devices used in this [activatingFlow] are removed from the other [flows]. This ensures that
     * no two [Flow]s try to use one device at a time.
     */
    fun activate(activatingFlow: Flow<*>) {
        flows += activatingFlow // add to Set<Flow>

        activatingFlow.useAllDevices()
        flows.filter { it != activatingFlow }.forEach { otherFlow ->
            otherFlow.usedDevices.removeAll { device -> device in activatingFlow.usedDevices }
        }
        activatingFlow.onResume()
    }

    fun runBlocking() {
        while (true) {
            if (clockMark.elapsedNow() > DURATION_PER_TICK) {
                flows.forEach { it.onTick() }
                clockMark = MonoClock.markNow()
            }

            TimeUnit.MILLISECONDS.sleep(SLEEP_TIME_MILLIS)
        }
    }
}