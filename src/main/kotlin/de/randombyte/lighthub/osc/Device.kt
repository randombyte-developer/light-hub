package de.randombyte.lighthub.osc

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.devices.MetaConfig
import kotlin.reflect.KClass
import kotlin.reflect.KFunction2

/**
 * A thing that receives messages from this program over an [OscChannel]. The [oscBasePath] will be used as part of the
 * path on the various [OscChannel]s in the [oscChannelList].
 */
abstract class Receiver(val oscBasePath: String) {
    abstract val oscChannelList: OscChannelList
}

typealias DeviceConstructor<T> = KFunction2<Int, Int, T> // number, dmx

/**
 * A device which receives messages from this program over OSC and then renders its state to a DMX universe with the
 * help of QLC+. The combination of [type] and [number] must be unique across all [Device]s.
 */
abstract class Device(
    val type: Type<out Device>,
    val number: Int,
    val dmxAddress: Int
) : Receiver("${type.id}/$number") {

    /**
     * Static information about the device which is the same for all devices of this type.
     */
    interface Type<T : Device> {
        val clazz: KClass<T>
        val constructor: DeviceConstructor<T>
        val id: String // for internal purposes, config file naming and logging
        val channelsCount: Int
        val metaConfig: ConfigHolder<MetaConfig>
        val configs: List<ConfigHolder<*>> get() = emptyList() // overwrite if the device can be configured

        fun reloadConfigs() {
            configs.forEach { it.reload() }
        }
    }

    val addressRange = dmxAddress until dmxAddress + type.channelsCount

    val shortNameForDisplay = type.metaConfig.config.`short-name` + number
}