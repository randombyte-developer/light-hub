package de.randombyte.lighthub.show.masterflows

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.devices.features.MasterDimmerFeature
import de.randombyte.lighthub.show.DevicesManager.ledBars
import de.randombyte.lighthub.show.FlowManager
import de.randombyte.lighthub.show.flows.intensity.IntensityFlow
import de.randombyte.lighthub.show.flows.intensity.IntensityFlow.Mode.Sawtooth
import de.randombyte.lighthub.utils.flatten
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SawToothIntensity : MasterFlow<Any?>(isFallback = true, devices = flatten<MasterDimmerFeature>(ledBars)) {
    override val configFolderName = "saw-tooth-intensity"

    override val configs: List<ConfigHolder<*>> = listOf()

    override fun onActivate() {
        super.onActivate()

        IntensityFlow.mode = Sawtooth
        FlowManager.addDevicesToFlow(IntensityFlow, ledBars)
    }
}