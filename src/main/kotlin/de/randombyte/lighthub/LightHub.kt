package de.randombyte.lighthub

import de.randombyte.lighthub.config.Configs
import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.qlc.QlcShowFileGenerator
import de.randombyte.lighthub.show.ThatShow
import java.nio.file.Paths

fun main(args: Array<String>) {
    Configs.setup()

    if (args.getOrNull(0) == "gen-qlc") {
        val path = Paths.get("LightHubShow.qxw").toAbsolutePath()
        QlcShowFileGenerator.generate(path, ThatShow.lights)
        println("Generated file: $path")
        return
    }

    LightHub.run()
}

object LightHub {
    fun run() {
        val akai = Akai.findBestMatch() ?: throw RuntimeException("Midi unavailable!")

        if (!akai.open()) throw RuntimeException("Midi unavailable!")

        ThatShow.setup(akai)
    }
}