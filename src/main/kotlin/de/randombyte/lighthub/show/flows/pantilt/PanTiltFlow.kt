package de.randombyte.lighthub.show.flows.pantilt

import de.randombyte.lighthub.osc.devices.features.PanTiltFeature
import de.randombyte.lighthub.show.flows.Flow
import kotlin.random.Random
import kotlin.time.ExperimentalTime

@ExperimentalTime
class PanTiltFlow(acceptedDevices: List<PanTiltFeature>) : Flow<PanTiltFeature>(acceptedDevices) {
    override fun onActivate(device: PanTiltFeature) {
        changePanTilt(device)
    }

    override fun onBeat(beat: ULong, device: PanTiltFeature) {
        with(device.panTiltBounds) {
           /* val specificDeviceOffset = `change-beats-offset` * (device as Device).number
            if ((beat + specificDeviceOffset.toUInt()).multipleOf(`change-every-n-beats`)) {
                changePanTilt(device)
            }*/
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