package de.randombyte.lighthub.config.serializer

import com.google.common.reflect.TypeToken
import de.randombyte.lighthub.config.Color
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer

class ColorTypeSerializer : TypeSerializer<Color> {
    override fun deserialize(type: TypeToken<*>, node: ConfigurationNode): Color {
        when (type.rawType) {
            Color.Rgb::class.java -> {
                //mapOf()
            }
        }

        TODO()
    }

    override fun serialize(type: TypeToken<*>, color: Color?, node: ConfigurationNode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}