package de.randombyte.lighthub.config.serializer

import io.github.config4k.registerCustomType

object CustomTypes {
    fun register() {
        registerCustomType(UByteCustomType)
    }
}