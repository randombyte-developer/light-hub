package de.randombyte.lighthub

import de.randombyte.lighthub.dmx.AdjPar
import de.randombyte.lighthub.dmx.LedBar
import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.qlc.QlcShowFileGenerator
import java.nio.file.Paths

val deviceTypes = listOf(AdjPar, LedBar)

fun main(args: Array<String>) {

    deviceTypes.forEach { it.config.reload() }

    if (args.getOrNull(0) == "gen-qlc") {
        val path = Paths.get("LightHubShow.qxw").toAbsolutePath()
        QlcShowFileGenerator.generate(path, deviceTypes)
        println("Generated file: $path")

        return
    }

    val akai = Akai.findBestMatch() ?: throw RuntimeException("Midi unavailable!")

    if (!akai.open()) throw RuntimeException("Midi unavailable!")

    akai.setListener { signal ->
        println(signal)
    }

}

class LightHub {

}