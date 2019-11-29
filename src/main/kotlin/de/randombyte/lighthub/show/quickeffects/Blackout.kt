package de.randombyte.lighthub.show.quickeffects

import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.show.DevicesManager.lights
import de.randombyte.lighthub.show.ThatShow
import kotlin.time.ExperimentalTime

@ExperimentalTime
object Blackout : QuickEffect() {
    override fun activate() {
        super.activate()

        lights.forEach { (it as? ShutterFeature)?.noLight() }
        ThatShow.blockEveryOscMessage = true
    }

    override fun deactivate() {
        ThatShow.blockEveryOscMessage = false
        super.deactivate()
    }
}