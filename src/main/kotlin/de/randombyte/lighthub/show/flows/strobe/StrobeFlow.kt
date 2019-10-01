package de.randombyte.lighthub.show.flows.strobe

import de.randombyte.lighthub.osc.devices.Scanner
import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.osc.devices.features.StrobeFeature
import de.randombyte.lighthub.show.flows.Flow
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow.Speed.Fast
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow.Speed.Slow
import kotlin.time.ExperimentalTime

@ExperimentalTime
class StrobeFlow(devices: List<ShutterFeature>) : Flow<ShutterFeature>(devices) {

    enum class Speed { Slow, Fast }
    var speed = Fast

    override fun onDeviceResume(device: ShutterFeature) {
        if (device is Scanner) {
            device.noLight()
            return
        }

        device.fullIntensity()
        (device as ColorFeature).setColor(device.colors.getValue(device.colorCategories.strobe))

        // todo better
        when (speed) {
            Slow -> (device as? StrobeFeature)?.slowStrobe()
            Fast -> (device as? StrobeFeature)?.fastStrobe()
        }
    }
}