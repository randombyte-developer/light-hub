package de.randombyte.lighthub

import de.randombyte.lighthub.dmx.DmxChannel

abstract class Transition(val changeTo: Map<DmxChannel, UByte>, val durationMs: Long) : Animation() {
    val firstValues = changeTo.keys.map { it.lastValue }

    abstract val finished: Boolean
}