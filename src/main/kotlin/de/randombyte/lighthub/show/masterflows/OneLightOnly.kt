package de.randombyte.lighthub.show.masterflows

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.show.DevicesManager.lights
import kotlin.time.ExperimentalTime

@ExperimentalTime
object OneLightOnly : MasterFlow<ColorFeature>(isFallback = true, devices = lights as List<ColorFeature>) {

    override val configFolderName = "one-light-only"

    override val configs = emptyList<ConfigHolder<*>>()

    override fun onActivate() {
        super.onActivate()

        devices.forEach { (it as ShutterFeature).noLight() }
        devices.random().apply {
            (this as ShutterFeature).fullIntensity()
            setColor(selectedColorSet.first())
        }
    }
}