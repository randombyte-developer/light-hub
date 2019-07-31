package de.randombyte.lighthub.osc

abstract class Receiver(val oscBasePath: String) {
    fun createOscChannel(path: String, relativeDmxAddress: Int) = OscChannel("/$oscBasePath/$path", relativeDmxAddress)
}