package de.randombyte.lighthub.show.quickeffects

import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.osc.devices.features.StrobeFeature
import de.randombyte.lighthub.show.DevicesManager.hexClones
import de.randombyte.lighthub.show.DevicesManager.hexPars
import de.randombyte.lighthub.show.DevicesManager.ledBars
import de.randombyte.lighthub.show.DevicesManager.quadPhases
import de.randombyte.lighthub.show.DevicesManager.tsssPars
import de.randombyte.lighthub.show.FlowManager
import de.randombyte.lighthub.show.quickeffects.Strobe.Speed.Fast
import de.randombyte.lighthub.show.quickeffects.Strobe.Speed.Slow
import de.randombyte.lighthub.utils.flatten
import kotlin.time.ExperimentalTime

@ExperimentalTime
object Strobe : QuickEffect() {

    // even the locked devices are strobing
    val devices = flatten<StrobeFeature>(ledBars, tsssPars, hexPars, hexClones, quadPhases)

    enum class Speed { Slow, Fast }
    var speed = Fast

    override fun activate() {
        super.activate()

        devices.forEach { device ->
            (device as ShutterFeature).fullIntensity()
            (device as ColorFeature).setColor(device.colors.getValue(device.colorSets.strobe))

            // todo better
            when (speed) {
                Slow -> device.slowStrobe()
                Fast -> device.fastStrobe()
            }
        }
    }

    override fun deactivate() {
        super.deactivate()

        devices.forEach { it.noStrobe() }
        // reset locked devices
        FlowManager.lockedDevices.forEach { (it as ShutterFeature).noLight() }
    }
}