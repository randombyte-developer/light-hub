package de.randombyte.lighthub.utils

object Ranges {

    val DMX_RANGE = 0..255
    val MIDI_RANGE = 0..127

    fun mapMidiToDmx(value: Int) = mapRange(from = MIDI_RANGE, to = DMX_RANGE, value = value)
    fun mapDmxToMidi(value: Int) = mapRange(from = DMX_RANGE, to = MIDI_RANGE, value = value)

    fun mapRange(from: IntRange, to: IntRange, value: Int): Int {
        require(value in from) { "Value is not in from-range!" }
        require(from.last != from.first) { "from-range must be larger than one!" }
        return to.first + (value - from.first) * (to.last - to.first) / (from.last - from.first)
    }
}

fun IntRange.intersects(other: IntRange) = this.first in other || this.last in other || other.first in this || other.last in this

val IntRange.length: Int get() = last - first

fun Int.coerceIn(range: IntRange, note: String): Int = if (this in range) this else {
    val coercedValue = this.coerceIn(range)
    warn("$note: Value($this) is not in range $range! Coerced to $coercedValue.")
    coercedValue
}

fun Double.coerceIn(range: ClosedFloatingPointRange<Double>, note: String): Double = if (this in range) this else {
    val coercedValue = this.coerceIn(range)
    warn("$note: Value($this) is not in range $range! Coerced to $coercedValue.")
    coercedValue
}