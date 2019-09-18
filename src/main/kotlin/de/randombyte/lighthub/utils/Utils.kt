package de.randombyte.lighthub.utils

// this function exists because for some reason the kotlin compiler can't infer the type when a generic is used
fun <T> flatten(vararg objects: List<T>) = objects.toList().flatten()

inline fun <reified A, reified B> List<*>.requireInstanceOf(): List<*> {
    require(this.all { it is A && it is B }) {
        "All objects must be of type ${A::class.simpleName} and ${B::class.simpleName}!"
    }

    return this
}