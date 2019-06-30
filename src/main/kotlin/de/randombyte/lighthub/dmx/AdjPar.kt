package de.randombyte.lighthub.dmx

import de.randombyte.lighthub.config.Color.Rgbwauv
import de.randombyte.lighthub.config.Color.Rgbwauv.Companion.new
import de.randombyte.lighthub.config.loader.toConfigHolder

class AdjPar(oscBasePath: String, startAddress: Int) : Light<Rgbwauv>(oscBasePath, startAddress) {

    class Config(
        colors: Map<String, Rgbwauv> = mapOf(
            "red" to new(r = 255, g = 0, b = 0, w = 0, a = 0, uv = 0),
            "green" to new(r = 0, g = 255, b = 0, w = 0, a = 0, uv = 0),
            "blue" to new(r = 0, g = 0, b = 255, w = 0, a = 0, uv = 0)
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
        override val configHolder = "adj-par.conf".toConfigHolder<Config>()
        override val channels = 12
    }

    override var color: Rgbwauv
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun blackout() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}