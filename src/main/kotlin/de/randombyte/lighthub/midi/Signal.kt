package de.randombyte.lighthub.midi

data class Signal constructor(val type: Int, val control: Int, val value: Int)