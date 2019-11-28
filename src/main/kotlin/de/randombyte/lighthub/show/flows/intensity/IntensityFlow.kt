package de.randombyte.lighthub.show.flows.intensity

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.features.MasterDimmerFeature
import de.randombyte.lighthub.show.DevicesManager.ledBars
import de.randombyte.lighthub.show.DevicesManager.pars
import de.randombyte.lighthub.show.flows.Flow
import de.randombyte.lighthub.show.flows.intensity.IntensityFlow.Mode.Sawtooth
import de.randombyte.lighthub.utils.Ranges.DMX_RANGE
import de.randombyte.lighthub.utils.flatten
import kotlin.time.ExperimentalTime

@ExperimentalTime
object IntensityFlow : Flow<MasterDimmerFeature>(acceptedDevices = flatten(pars, ledBars)) {

    enum class Mode { Sawtooth }
    var mode = Sawtooth

    override fun onTick(tick: ULong, device: MasterDimmerFeature) {
        if (mode == Sawtooth) {
            device.masterDimmer = (DMX_RANGE.last * getPercentUntilNextChange<IntensityAutoPatternsConfig>(tick, device as Device)).toInt()
        }
    }
}