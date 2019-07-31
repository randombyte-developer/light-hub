package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.config.ConfigHolder
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.Receiver
import de.randombyte.lighthub.osc.devices.features.ConfigurableFeature
import kotlin.reflect.KClass
import kotlin.reflect.KFunction2

typealias DeviceConstructor<T> = KFunction2<Int, Int, T> // number, dmx

abstract class Device(
    val type: Type<out Device>,
    val number: Int,
    val dmxAddress: Int
) : Receiver("${type.id}/$number") {

    interface Type<T : Device> {
        val clazz: KClass<T>
        val constructor: DeviceConstructor<T>
        val id: String // for internal purposes and config file naming
        val channels: Int // number of dmx channels
        val metaConfigHolder: ConfigHolder<MetaConfig>
    }

    init {
        (this as? ConfigurableFeature)?.reloadConfig()
    }

    val addressRange = dmxAddress until dmxAddress + type.channels

    abstract val oscChannelList: OscChannelList
}