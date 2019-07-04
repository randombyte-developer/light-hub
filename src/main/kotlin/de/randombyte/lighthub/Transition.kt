package de.randombyte.lighthub

import de.randombyte.lighthub.osc.OscChannel

abstract class Transition(val changeTo: Map<OscChannel, UByte>, val durationMs: Int) : Animation() {
    val firstValues = changeTo.keys.map { it.lastValue }

    abstract val finished: Boolean
}