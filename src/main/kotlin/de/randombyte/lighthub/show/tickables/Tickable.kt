package de.randombyte.lighthub.show.tickables

interface Tickable {
    fun onTick(tick: ULong) {}
    fun onBeat(beat: ULong) {}
}