package de.randombyte.lighthub.show

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.show.events.SelectedColorSet
import tornadofx.FX
import kotlin.time.ExperimentalTime

@ExperimentalTime
object ColorSelector {
    var selectedColorSetId = "set-1"
        set(value) {
            field = value

            FX.eventbus.fire(SelectedColorSet(value))
        }

    val colorSetSelectedColorIndex = mutableMapOf<Device, ULong>().withDefault { 0.toULong() }

    fun selectNextColor(device: Device) {
        colorSetSelectedColorIndex[device] = (colorSetSelectedColorIndex[device] ?: 0.toULong()) + 1.toULong()
    }
}