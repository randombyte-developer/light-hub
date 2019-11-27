package de.randombyte.lighthub.utils

import java.util.*

// this function exists because for some reason the kotlin compiler can't infer the type when a generic is used
fun <T> flatten(vararg objects: List<T>) = objects.toList().flatten()

inline fun <reified A, reified B> List<*>.requireInstanceOf(): List<A> {
    require(this.all { it is A && it is B }) {
        "All objects must be of type ${A::class.simpleName} and ${B::class.simpleName}!"
    }

    return this as List<A>
}

fun <T : Any?> Queue<T>.pollForEach(action: (T) -> Unit) {
    var obj: T? = poll()
    while (obj != null) {
        action(obj)
        obj = poll()
    }
}

fun List<*>.containsIgnoreType(element: Any) = contains(element)

fun <T> List<T>.getElementWrappedAround(index: ULong) = get(index.rem(size).toInt())