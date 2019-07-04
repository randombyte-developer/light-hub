package de.randombyte.lighthub.utils

class CycleList<T>(val list: List<T>) {

    private var i = 0

    /**
     * @return true when the list just wrapped around
     */
    fun next(): Boolean {
        i = (i +1) % list.size
        return i == 0
    }

    fun get(): T = list[i]
}