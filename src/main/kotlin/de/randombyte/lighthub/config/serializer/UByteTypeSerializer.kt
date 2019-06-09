package de.randombyte.lighthub.config.serializer

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer

class UByteTypeSerializer : TypeSerializer<UByte> {
    override fun deserialize(type: TypeToken<*>, node: ConfigurationNode) = node.int.toUByte()

    override fun serialize(type: TypeToken<*>, value: UByte?, node: ConfigurationNode) {
        println(value)
        node.value = value?.toString()
    }
}