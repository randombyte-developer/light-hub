package de.randombyte.lighthub.show.flows.pantilt

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.features.PanTiltFeature
import de.randombyte.lighthub.show.DevicesManager.scanners
import de.randombyte.lighthub.show.flows.Flow
import kotlin.random.Random
import kotlin.time.ExperimentalTime

@ExperimentalTime
object PanTiltFlow : Flow<PanTiltFeature>(acceptedDevices = scanners) {
    override fun onActivate(device: PanTiltFeature) {
        changePanTilt(device)
    }

    override fun onTick(tick: ULong, device: PanTiltFeature) {
        if (isOnChange<PanTiltAutoPatternsConfig>(tick, device as Device)) {
            changePanTilt(device)
        }
    }

    private fun changePanTilt(device: PanTiltFeature) {
        val position = with (device.panTiltBounds) {
            PanTiltFeature.Position(
                pan = Random.nextInt(from = `pan-min`, until = `pan-max` + 1),
                tilt = Random.nextInt(from = `tilt-min`, until = `tilt-max` + 1)
            )
        }
        device.position = position
    }
}