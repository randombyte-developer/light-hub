package de.randombyte.lighthub.dmx

import de.randombyte.lighthub.config.Color.Rgbwauv
import de.randombyte.lighthub.config.Color.Rgbwauv.Companion.new
import de.randombyte.lighthub.config.loader.ConfigManager
import de.randombyte.lighthub.config.loader.toConfigHolder
import de.randombyte.lighthub.config.loader.toConfigLoader
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

class AdjPar(oscBasePath: String, startAddress: Int) : Light<Rgbwauv>(oscBasePath, startAddress) {

    @ConfigSerializable class Config(
        colors: Map<String, Rgbwauv> = mapOf(
            "red" to new(r = 255u, g = 0u, b = 0u, w = 0u, a = 0u, uv = 0u),
            "green" to new(r = 0u, g = 255u, b = 0u, w = 0u, a = 0u, uv = 0u),
            "blue" to new(r = 0u, g = 0u, b = 255u, w = 0u, a = 0u, uv = 0u)
        ),
        addresses: List<UByte> = listOf(223u), // todo
        meta: Meta = Meta(
            manufacturer = "American DJ",
            model = "Mega Hex Par",
            mode = "12 channel",
            name = "ADJPar"
        )
    ) : Light.Config<Rgbwauv>(meta, addresses, colors)

    companion object : Type<Rgbwauv> {
        override val config = ConfigManager("adj-par.conf".toConfigLoader(), Config::class.java).toConfigHolder()
        override val channels = 12
        override val colors = emptyMap<String, Rgbwauv>() //config.get().colors
    }

    override fun blackout() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}