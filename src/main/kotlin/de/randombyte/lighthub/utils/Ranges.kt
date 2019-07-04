package de.randombyte.lighthub.utils

object Ranges {

    val DMX_RANGE = 0..255
    val MIDI_RANGE = 0..127

    fun mapMidiToDmx(value: Int) = Ranges.mapRange(from = MIDI_RANGE, to = DMX_RANGE, value = value)
    fun mapDmxToMidi(value: Int) = Ranges.mapRange(from = DMX_RANGE, to = MIDI_RANGE, value = value)

    fun mapRange(from: IntRange, to: IntRange, value: Int): Int {
        if (value !in from) throw IllegalArgumentException("Value is not in from-range!")
        if (from.endInclusive == from.start) throw IllegalArgumentException("from-range must be larger than one!")
        return to.start + (value - from.start) * (to.endInclusive - to.start) / (from.endInclusive - from.start)
    }
}