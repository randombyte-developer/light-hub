package de.randombyte.lighthub.show.quickeffects

import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.show.ThatShow
import de.randombyte.lighthub.show.quickeffects.Strobe.devices
import kotlin.time.ExperimentalTime

@ExperimentalTime
object Blackout : QuickEffect() {
    override fun activate() {
        super.activate()

        devices.forEach { (it as? ShutterFeature)?.noLight() }
        ThatShow.blockEveryOscMessage = true
    }

    override fun deactivate() {
        ThatShow.blockEveryOscMessage = false
        super.deactivate()
    }
}