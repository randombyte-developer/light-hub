package de.randombyte.lighthub.show

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.colors.Color
import de.randombyte.lighthub.show.events.UpdateColor
import de.randombyte.lighthub.utils.getElementWrappedAround
import tornadofx.FX
import kotlin.time.ExperimentalTime

@ExperimentalTime
object ColorSelector {
    var selectedColorSetId = "set-1"
        set(value) {
            field = value
            colorSetSelectedColorIndex.clear() // start with the first of the color set when a set is activated
            requestColorUpdate()
        }

    val colorSetSelectedColorIndex = mutableMapOf<Device, ULong>().withDefault { 0.toULong() }

    fun selectNextColor(device: Device) {
        colorSetSelectedColorIndex[device] = (colorSetSelectedColorIndex[device] ?: 0.toULong()) + 1.toULong()
    }

    fun getSelectedColor(device: ColorFeature): Color {
        val colorIndex = colorSetSelectedColorIndex.getValue(device as Device)
        val colorId = device.colorSets.all.getValue(selectedColorSetId).getElementWrappedAround(colorIndex)
        return device.colors.getValue(colorId)
    }

    fun requestColorUpdate() {
        FX.eventbus.fire(UpdateColor())
    }
}