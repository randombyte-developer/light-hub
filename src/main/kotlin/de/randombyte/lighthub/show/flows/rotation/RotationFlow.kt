package de.randombyte.lighthub.show.flows.rotation

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.features.RotationFeature
import de.randombyte.lighthub.show.DevicesManager.quadPhases
import de.randombyte.lighthub.show.flows.Flow
import kotlin.time.ExperimentalTime

@ExperimentalTime
object RotationFlow : Flow<RotationFeature>(acceptedDevices = quadPhases) {
    override fun onActivate(device: RotationFeature) {
        changeRotation(device)
    }

    override fun onTick(tick: ULong, device: RotationFeature) {
        if (isOnChange<RotationAutoPatternsConfig>(tick, device as Device)) {
            changeRotation(device)
        }
    }

    private fun changeRotation(device: RotationFeature) {
        device.rotationSpeed = device.rotationSpeeds.normal.random()
    }
}