package de.randombyte.lighthub.utils

import de.randombyte.lighthub.osc.devices.Device
import de.randombyte.lighthub.osc.devices.features.Feature

fun <A, B> Array<out Pair<A, B>>.forEach(action: (A, B) -> Unit) = toList().forEach { (a, b) -> action(a, b) }

inline fun <reified T : Feature> List<Device>.getFeaturesByType() = flatMap { it.getFeaturesByType<T>() }