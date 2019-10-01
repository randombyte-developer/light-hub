package de.randombyte.lighthub.show.flows.strobe

import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.RotationFeature
import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.osc.devices.features.StrobeFeature
import de.randombyte.lighthub.show.flows.Flow
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow.Speed.Fast
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow.Speed.Slow
import kotlin.time.ExperimentalTime

@ExperimentalTime
class StrobeFlow(devices: List<StrobeFeature>) : Flow<StrobeFeature>(devices) {

    enum class Speed { Slow, Fast }
    var speed = Fast

    override fun onResume() {
        usedDevices.forEach { device ->
            (device as ColorFeature).setColor(device.colors.getValue(device.colorCategories.strobe))
            (device as? ShutterFeature)?.fullIntensity()
            (device as? RotationFeature)?.rotationSpeed = (device as RotationFeature).rotationSpeeds.none

            // todo better
            when (speed) {
                Slow -> device.slowStrobe()
                Fast -> device.fastStrobe()
            }
        }
    }
}