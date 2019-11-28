package de.randombyte.lighthub.show.masterflows

import de.randombyte.lighthub.show.FlowManager
import de.randombyte.lighthub.show.MasterFlowManager
import de.randombyte.lighthub.show.tickables.StoppableTickable
import tornadofx.Controller
import tornadofx.EventContext
import tornadofx.FXEvent
import kotlin.time.ExperimentalTime

@ExperimentalTime
abstract class MasterFlow<T>(val isFallback: Boolean, val devices: List<T>) : Controller(), StoppableTickable {

    companion object {
        const val FLOWS_CONFIG_FOLDER = "flows"
    }

    abstract val configFolderName: String

    override fun onActivate() {
        FlowManager.removeAllDevicesFromFlows()
    }

    // MasterFlows only activate and don't tick, todo: don't inherit Tickable
    final override fun onTick(tick: ULong) { }
    final override fun onBeat(beat: ULong) { }

    protected inline fun <reified T : FXEvent> subscribeIfActive(crossinline action: EventContext.(T) -> Unit) {
        subscribe<T> {
            if (MasterFlowManager.active == this@MasterFlow) action(it)
        }
    }
}