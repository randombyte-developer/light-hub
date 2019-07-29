package de.randombyte.lighthub.osc.devices.features

interface StrobeFeature : Feature {
    fun on()
    fun off()

    var speed: Int
}