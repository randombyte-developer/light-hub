package de.randombyte.lighthub.osc

import de.randombyte.lighthub.config.CONFIG_PATH
import de.randombyte.lighthub.config.ConfigDescription
import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.config.ConfigHoldersCache
import de.randombyte.lighthub.osc.devices.MetaConfig
import de.randombyte.lighthub.show.MasterFlowManager
import de.randombyte.lighthub.show.masterflows.MasterFlow
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.reflect.KFunction2
import kotlin.reflect.full.companionObjectInstance
import kotlin.time.ExperimentalTime

/**
 * A thing that receives messages from this program over an [OscChannel]. The [oscBasePath] will be used as part of the
 * path on the various [OscChannel]s in the [oscChannelList].
 */
@ExperimentalTime
abstract class Receiver(val oscBasePath: String) {
    abstract val oscChannelList: OscChannelList
}

typealias DeviceConstructor<T> = KFunction2<Int, Int, T> // number, dmx

/**
 * A device which receives messages from this program over OSC and then renders its state to a DMX universe with the
 * help of QLC+. The combination of [type] and [number] must be unique across all [Device]s.
 */
@ExperimentalTime
abstract class Device(
    val type: Type<out Device>,
    val number: Int,
    val dmxAddress: Int
) : Receiver("${type.id}/$number") {

    /**
     * Static information about the device which is the same for all devices of this type.
     */
    @ExperimentalTime
    abstract class Type<T : Device> {
        abstract val clazz: KClass<T>
        abstract val constructor: DeviceConstructor<T>
        abstract val id: String // for internal purposes, config file naming and logging
        abstract val channelsCount: Int

        val configPath: Path get() = CONFIG_PATH.resolve(id)
        val currentMasterFlowConfigPath: Path
            get() = configPath
                .resolve(MasterFlow.FLOWS_CONFIG_FOLDER)
                .resolve(MasterFlowManager.active.configFolderName)
        abstract val metaConfig: ConfigHolder<MetaConfig>
        open val configs: List<ConfigHolder<*>> get() = emptyList() // overwrite if the device can be configured

        inline fun <reified C : Any> getCurrentMasterFlowConfig(): ConfigHolder<C> {
            return ConfigHoldersCache.getOrLoadConfigHolder(
                folder = currentMasterFlowConfigPath,
                name = (C::class.companionObjectInstance!! as ConfigDescription).fileName
            )
        }
    }

    val addressRange = dmxAddress until dmxAddress + type.channelsCount

    val shortNameForDisplay = type.metaConfig.config.`short-name` + number

    open val flowConfigs: List<ConfigHolder<*>> = emptyList()
}