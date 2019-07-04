package de.randombyte.lighthub

import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.midi.akai.Control
import de.randombyte.lighthub.osc.QlcPlus
import de.randombyte.lighthub.osc.dmx.LedBar

/**
 * One specific show with this many lights and the [Akai] as the controller.
 */
object ThatShow {

    fun setup(akai: Akai) {
        akai.registerControl(object : Control.Button.SimpleButton(16) {
            override fun onUp() {
                println("Up")
            }

            override fun onDown() {
                println("Down")
            }
        })
    }

    val ledBar1 = LedBar(number = 1, startAddress = 29)

    val ledBars = listOf(ledBar1)

    val lights = listOf(ledBars).flatten()

    var blackout: Boolean = false
        set(value) {
            if (QlcPlus.oscBlackout.lastValue != value.toInt()) {
                QlcPlus.oscBlackout.sendValue(value.toInt())
                field = value
            }
        }

    private fun Boolean.toInt() = if (this) 1 else 0
}