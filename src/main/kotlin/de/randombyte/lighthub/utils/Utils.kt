package de.randombyte.lighthub.utils

fun <A, B> Array<out Pair<A, B>>.forEach(action: (A, B) -> Unit) = toList().forEach { (a, b) -> action(a, b) }