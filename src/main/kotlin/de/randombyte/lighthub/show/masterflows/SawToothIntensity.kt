package de.randombyte.lighthub.show.masterflows

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.MasterDimmerFeature
import de.randombyte.lighthub.show.DevicesManager.ledBars
import de.randombyte.lighthub.show.FlowManager
import de.randombyte.lighthub.show.events.UpdateColor
import de.randombyte.lighthub.show.flows.intensity.IntensityFlow
import de.randombyte.lighthub.show.flows.intensity.IntensityFlow.Mode.Sawtooth
import de.randombyte.lighthub.utils.flatten
import kotlin.time.ExperimentalTime

@ExperimentalTime
object SawToothIntensity : MasterFlow<Any?>(isFallback = true, devices = flatten<MasterDimmerFeature>(ledBars)) {
    override val configFolderName = "saw-tooth-intensity"

    init {
        subscribeIfActive<UpdateColor> {
            OneLightOnly.onActivate()
        }
    }

    override fun onActivate() {
        super.onActivate()

        (devices as List<ColorFeature>).forEach {
            it.setColor(it.selectedColorSet.first())
        }
        IntensityFlow.mode = Sawtooth
        FlowManager.addDevicesToFlow(IntensityFlow, devices as List<Device>)
    }
}