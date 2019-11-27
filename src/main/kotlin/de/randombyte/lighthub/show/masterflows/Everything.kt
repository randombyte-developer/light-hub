package de.randombyte.lighthub.show.masterflows

import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.show.DevicesManager.hexClones
import de.randombyte.lighthub.show.DevicesManager.hexPars
import de.randombyte.lighthub.show.DevicesManager.ledBars
import de.randombyte.lighthub.show.DevicesManager.lights
import de.randombyte.lighthub.show.DevicesManager.quadPhases
import de.randombyte.lighthub.show.DevicesManager.scanners
import de.randombyte.lighthub.show.FlowManager
import de.randombyte.lighthub.show.flows.colorchanger.ColorChangerFlow
import de.randombyte.lighthub.show.flows.pantilt.PanTiltFlow
import de.randombyte.lighthub.show.flows.rotation.RotationFlow
import kotlin.time.ExperimentalTime

@ExperimentalTime
object Everything : MasterFlow<ColorFeature>(isFallback = true, devices = lights as List<ColorFeature>) {

    override val configFolderName = "everything"

    override fun onActivate() {
        super.onActivate()

        FlowManager.addDevicesToFlow(ColorChangerFlow, quadPhases)
        FlowManager.addDevicesToFlow(RotationFlow, quadPhases)

        FlowManager.addDevicesToFlow(ColorChangerFlow, hexPars)

        FlowManager.addDevicesToFlow(ColorChangerFlow, hexClones)

        FlowManager.addDevicesToFlow(ColorChangerFlow, ledBars)

        FlowManager.addDevicesToFlow(ColorChangerFlow, scanners)
        FlowManager.addDevicesToFlow(PanTiltFlow, scanners)
    }
}