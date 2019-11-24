package de.randombyte.lighthub.show

import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.show.AkaiControls.akai
import de.randombyte.lighthub.show.DevicesManager.hexClones
import de.randombyte.lighthub.show.DevicesManager.hexPars
import de.randombyte.lighthub.show.DevicesManager.ledBars
import de.randombyte.lighthub.show.DevicesManager.lights
import de.randombyte.lighthub.show.DevicesManager.quadPhases
import de.randombyte.lighthub.show.DevicesManager.scanners
import de.randombyte.lighthub.show.DevicesManager.tsssPars
import de.randombyte.lighthub.show.FlowManager.colorChangeFlow
import de.randombyte.lighthub.show.FlowManager.panTiltFlow
import de.randombyte.lighthub.show.FlowManager.rotationFlow
import de.randombyte.lighthub.show.events.SelectedColorSet
import de.randombyte.lighthub.show.masterflows.OneLightOnly
import de.randombyte.lighthub.show.strobe.Strobe
import de.randombyte.lighthub.show.tickables.Tickable
import de.randombyte.lighthub.show.tickables.Ticker
import de.randombyte.lighthub.ui.events.ToggledMasterEvent
import de.randombyte.lighthub.ui.events.ToggledMasterEvent.MasterToggleDeviceCategory
import de.randombyte.lighthub.utils.flatten
import de.randombyte.lighthub.utils.pollForEach
import tornadofx.Controller
import tornadofx.FX
import kotlin.time.ExperimentalTime

/**
 * One specific show with this many lights and the [Akai] as the controller.
 */
@ExperimentalTime
object ThatShow : Controller() {

    val strobe = Strobe(flatten(ledBars, tsssPars, hexPars, hexClones, quadPhases, scanners))

    val deviceCategoryMasterActivated = MasterToggleDeviceCategory.values().map { it to true }.toMap().toMutableMap()

    val masterFlowsKeyboardMapping = mapOf(
        "G" to OneLightOnly
    )

    fun init() {
        registerTickables()
        subscribeToEvents()

        MasterFlowManager.activate(masterFlowsKeyboardMapping.getValue("G"))

        // cause the event ToggledMasterEvent to be fired to set the color in the UI
        repeat(2) { MasterToggleDeviceCategory.values().forEach { toggleMaster(it) } }
    }

    private fun registerTickables() {
        Ticker.register(colorChangeFlow)
        Ticker.register(rotationFlow)
        Ticker.register(panTiltFlow)

        masterFlowsKeyboardMapping.values.forEach { Ticker.register(it) }

        Ticker.register(object : Tickable {
            override fun onTick(tick: ULong) {
                akai.processCachedSignals()

                // run Runnables which were queued by other Threads to be executed on this Thread
                ShowThreadRunner.runnables.pollForEach { it.run() }
            }
        })
    }

    private fun subscribeToEvents() {
        subscribe<SelectedColorSet> {
            MasterFlowManager.active.onActivate() // reactivate to immediately propagate the color set change
        }
    }

    fun blackout() = blackout(lights as List<ShutterFeature>)

    fun blackout(devices: List<ShutterFeature>) {
        devices.forEach { it.noLight() }
    }

    fun toggleMaster(deviceCategory: MasterToggleDeviceCategory) {
        val activated = !deviceCategoryMasterActivated.getValue(deviceCategory)
        deviceCategoryMasterActivated[deviceCategory] = activated

        lights
            .filter { it.type in deviceCategory.types }
            .forEach { device ->
                if (activated) {
                    FlowManager.unlockDevice(device)
                    MasterFlowManager.activateFallback()
                } else {
                    (device as ShutterFeature).noLight()
                    FlowManager.lockDevice(device)
                }
        }

        FX.eventbus.fire(ToggledMasterEvent(activated, deviceCategory))
    }
}