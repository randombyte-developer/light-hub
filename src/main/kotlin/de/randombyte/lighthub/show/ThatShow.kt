package de.randombyte.lighthub.show

import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.show.AkaiControls.akai
import de.randombyte.lighthub.show.DevicesManager.lights
import de.randombyte.lighthub.show.tickables.Tickable
import de.randombyte.lighthub.show.tickables.Ticker
import de.randombyte.lighthub.ui.events.ToggledMasterEvent
import de.randombyte.lighthub.ui.events.ToggledMasterEvent.MasterToggleDeviceCategory
import de.randombyte.lighthub.utils.pollForEach
import tornadofx.Controller
import tornadofx.FX
import kotlin.time.ExperimentalTime

/**
 * One specific show with this many lights and the [Akai] as the controller.
 */
@ExperimentalTime
object ThatShow : Controller() {

    var blockEveryOscMessage = false

    val deviceCategoryMasterActivated = MasterToggleDeviceCategory.values().map { it to true }.toMap().toMutableMap()

    fun init() {
        registerTickables()

        MasterFlowManager.activate(KeyboardControls.masterFlows.getValue("Q"))

        // cause the event ToggledMasterEvent to be fired to set the color in the UI
        repeat(2) { MasterToggleDeviceCategory.values().forEach { toggleMaster(it) } }
    }

    private fun registerTickables() {
        FlowManager.flows.forEach { Ticker.register(it) }

        KeyboardControls.masterFlows.values.forEach { Ticker.register(it) }

        Ticker.register(object : Tickable {
            override fun onTick(tick: ULong) {
                akai.processCachedSignals()

                // run Runnables which were queued by other Threads to be executed on this Thread
                ShowThreadRunner.runnables.pollForEach { it.run() }
            }
        })
    }

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