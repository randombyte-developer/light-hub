package de.randombyte.lighthub.ui.events

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.*
import tornadofx.FXEvent
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ToggledMasterEvent(val activated: Boolean, val deviceCategory: MasterToggleDeviceCategory) : FXEvent() {
    @ExperimentalTime
    enum class MasterToggleDeviceCategory(vararg val types: Device.Type<*>) {
        HexPars(HexPar), OtherPars(HexClone, TsssPar), LedBars(LedBar), Quads(QuadPhase), Scanners(Scanner)
    }
}