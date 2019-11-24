package de.randombyte.lighthub.show.flows.rotation

import de.randombyte.lighthub.osc.devices.features.RotationFeature
import de.randombyte.lighthub.show.flows.Flow
import kotlin.time.ExperimentalTime

@ExperimentalTime
class RotationFlow(acceptedDevices: List<RotationFeature>) : Flow<RotationFeature>(acceptedDevices) {
    override fun onActivate(device: RotationFeature) {
        changeRotation(device)
    }

    override fun onBeat(beat: ULong, device: RotationFeature) {
        /*with(device.rotationAutoPatterns) {
            val specificDeviceOffset = `change-beats-offset` * (device as Device).number
            if ((beat + specificDeviceOffset.toUInt()).multipleOf(`change-every-n-beats`)) {
                changeRotation(device)
            }
        }*/
    }

    private fun changeRotation(device: RotationFeature) {
        device.rotationSpeed = device.rotationSpeeds.normal.random()
    }
}