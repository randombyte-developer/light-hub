package de.randombyte.lighthub.osc

abstract class Receiver(val oscBasePath: String) {
    fun String.toOscChannel() = OscChannel("/$oscBasePath/$this")
}