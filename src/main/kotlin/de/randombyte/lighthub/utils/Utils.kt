package de.randombyte.lighthub.utils

import de.randombyte.lighthub.osc.devices.Device

fun <A, B> Array<out Pair<A, B>>.forEach(action: (A, B) -> Unit) = toList().forEach { (a, b) -> action(a, b) }

// this function exists because for some reason the kotlin compiler can't infer the type when a generic is used
fun <T> flatten(vararg objects: List<T>) = objects.toList().flatten() as List<T>