package de.randombyte.lighthub.osc.devices

import de.randombyte.lighthub.osc.OscChannelMapping
import de.randombyte.lighthub.osc.Receiver
import de.randombyte.lighthub.osc.devices.features.Feature
import de.randombyte.lighthub.osc.devices.features.MetaFeature
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

abstract class Device(
    val type: Type,
    oscBasePath: String,
    val number: Int
) : Receiver("$oscBasePath/$number") {

    interface Type {
        val id: String
        val channels: Int
    }

    fun reloadConfigs() {
        (features + metaFeature).forEach { feature ->
            (feature as? Feature.Configurable)?.configHolders?.forEach {
                    configHolder -> configHolder.reload()
            }
        }
    }

    abstract val oscChannelMapping: OscChannelMapping

    abstract val metaFeature: MetaFeature

    abstract val features: List<Feature>

    inline fun <reified T : Feature> getFeaturesByType() = features.mapNotNull { it as? T }
}