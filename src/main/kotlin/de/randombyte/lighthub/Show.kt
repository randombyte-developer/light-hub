package de.randombyte.lighthub

import de.randombyte.lighthub.dmx.LedBar

class Show() {
    val ledBar1 = LedBar("/LedBar/1", startAddress = 29)

    val ledBars = listOf(ledBar1)

    val lights = ledBars

    fun blackout() {
        lights.forEach(LedBar::blackout)
    }
}