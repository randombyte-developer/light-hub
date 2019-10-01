package de.randombyte.lighthub.show.flows.rotation

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.features.RotationFeature
import de.randombyte.lighthub.show.flows.Flow
import de.randombyte.lighthub.utils.multipleOf
import kotlin.time.ExperimentalTime

@ExperimentalTime
class RotationFlow(assignedDevices: List<RotationFeature>) : Flow<RotationFeature>(assignedDevices) {
    override fun onDeviceResume(device: RotationFeature) {
        changeRotation(device)
    }

    override fun onDeviceBeat(beat: ULong, device: RotationFeature) {
        with(device.rotationAutoPatterns) {
            val specificDeviceOffset = `change-beats-offset` * (device as Device).number
            if ((beat + specificDeviceOffset.toUInt()).multipleOf(`change-every-n-beats`)) {
                changeRotation(device)
            }
        }
    }

    private fun changeRotation(device: RotationFeature) {
        device.rotationSpeed = device.rotationSpeeds.normal.random()
    }
}