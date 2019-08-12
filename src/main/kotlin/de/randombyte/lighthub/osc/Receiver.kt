package de.randombyte.lighthub.osc

abstract class Receiver(val oscBasePath: String) {
    val uniqueId = oscBasePath
}