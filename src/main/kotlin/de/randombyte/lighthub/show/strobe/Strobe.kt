package de.randombyte.lighthub.show.strobe

import de.randombyte.lighthub.osc.devices.Scanner
import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.osc.devices.features.StrobeFeature
import de.randombyte.lighthub.show.strobe.Strobe.Speed.Fast
import de.randombyte.lighthub.show.strobe.Strobe.Speed.Slow
import kotlin.time.ExperimentalTime

@ExperimentalTime
class Strobe(val devices: List<ShutterFeature>) {

    enum class Speed { Slow, Fast }

    fun activate(speed: Speed) {
        devices.forEach { device ->
            if (device is Scanner) {
                device.noLight() // for now Scanners deactivated
                return
            }

            device.fullIntensity()
            (device as ColorFeature).setColor(device.colors.getValue(device.colorSets.strobe))

            // todo better
            when (speed) {
                Slow -> (device as? StrobeFeature)?.slowStrobe()
                Fast -> (device as? StrobeFeature)?.fastStrobe()
            }
        }
    }
}