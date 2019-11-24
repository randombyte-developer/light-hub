package de.randombyte.lighthub.show.masterflows

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.show.FlowManager
import de.randombyte.lighthub.show.tickables.Tickable
import kotlin.time.ExperimentalTime

@ExperimentalTime
abstract class MasterFlow<T>(val isFallback: Boolean, val devices: List<T>) : Tickable {

    companion object {
        const val FLOWS_CONFIG_FOLDER = "flows"
    }

    abstract val configFolderName: String
    abstract val configs: List<ConfigHolder<*>>

    override fun onActivate() {
        FlowManager.removeAllDevicesFromFlows()
    }
}