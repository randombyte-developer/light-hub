package de.randombyte.lighthub.show.masterflows

import de.randombyte.lighthub.show.FlowManager
import de.randombyte.lighthub.show.tickables.Tickable
import kotlin.time.ExperimentalTime

@ExperimentalTime
abstract class MasterFlow<T>(val isFallback: Boolean, val devices: List<T>) : Tickable {

    companion object {
        const val FLOWS_CONFIG_FOLDER = "flows"
    }

    abstract val configFolderName: String

    override fun onActivate() {
        FlowManager.removeAllDevicesFromFlows()
    }

    // MasterFlows only activate and don't tick
    final override fun onTick(tick: ULong) { }
    final override fun onBeat(beat: ULong) { }
}