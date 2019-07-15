package de.randombyte.lighthub.utils

class CycleList<T>(val list: List<T>) {

    private var i = 0

    fun next() {
        i = (i + 1) % list.size
    }

    val value: T
        get() = list[i]
}