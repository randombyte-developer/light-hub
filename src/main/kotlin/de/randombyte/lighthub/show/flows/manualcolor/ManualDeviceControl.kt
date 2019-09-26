package de.randombyte.lighthub.show.flows.manualcolor

import de.randombyte.lighthub.midi.akai.Control
import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.features.RgbFeature
import de.randombyte.lighthub.osc.devices.features.RgbwFeature
import de.randombyte.lighthub.osc.devices.features.RgbwauvFeature
import de.randombyte.lighthub.show.flows.FlowManager
import de.randombyte.lighthub.show.flows.isClaimed
import de.randombyte.lighthub.show.tickables.Tickable
import kotlin.time.ExperimentalTime

/**
 * The ManualControlFlow can claim Devices for itself. Flows can't use these claimed Devices then.
 */
@ExperimentalTime
class ManualDeviceControl(val devices: List<Device>, val sendDisplayName: (String) -> Unit) : Tickable {

    private var index = 0
    var device = devices[0]
        private set

    fun onSelectNextDevice() = selectDevice(indexOffset = +1)
    fun onSelectPreviousDevice() = selectDevice(indexOffset = -1)

    fun onClaimDevice() {
        FlowManager.claimDevice(device)
        sendDeviceName()
    }

    fun onFreeDevice() {
        FlowManager.freeDevice(device)
        sendDeviceName()
    }

    fun onKnob1ChangeValue(control: Control.Potentiometer) {
        if (!device.isClaimed) return

        (device as? RgbFeature)?.run { setColor(getColor().plusRed(control.direction)) }
    }

    fun onKnob2ChangeValue(control: Control.Potentiometer) {
        if (!device.isClaimed) return

        (device as? RgbFeature)?.run { setColor(getColor().plusGreen(control.direction)) }
    }

    fun onKnob3ChangeValue(control: Control.Potentiometer) {
        if (!device.isClaimed) return

        (device as? RgbFeature)?.run { setColor(getColor().plusBlue(control.direction)) }
    }

    fun onKnob4ChangeValue(control: Control.Potentiometer) {
        if (!device.isClaimed) return

        (device as? RgbwFeature)?.run { setColor(getColor().plusWhite(control.direction)) }
    }

    fun onKnob5ChangeValue(control: Control.Potentiometer) {
        if (!device.isClaimed) return

        (device as? RgbwauvFeature)?.run { setColor(getColor().plusAmber(control.direction)) }
    }

    fun onKnob6ChangeValue(control: Control.Potentiometer) {
        if (!device.isClaimed) return

        (device as? RgbwauvFeature)?.run { setColor(getColor().plusUv(control.direction)) }
    }

    private fun selectDevice(indexOffset: Int) {
        index = Math.floorMod(index + indexOffset, devices.size)
        device = devices[index]

        sendDeviceName()
    }

    private fun sendDeviceName() {
        sendDisplayName((if (device.isClaimed) "#" else "") + device.shortNameForDisplay)
    }
}
