package de.randombyte.lighthub

abstract class Animation() {
    /**
     * Should be called at 20 Hz.
     */
    abstract fun tick()
}