package de.randombyte.lighthub

import de.randombyte.lighthub.config.GlobalConfigs
import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.qlc.QlcShowFileGenerator
import de.randombyte.lighthub.show.ThatShow
import de.randombyte.lighthub.show.flows.FlowTicker
import java.nio.file.Paths
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main(args: Array<String>) {
    GlobalConfigs.reload()

    if (args.getOrNull(0) == "gen-qlc") {
        val path = Paths.get("LightHubShow.qxw").toAbsolutePath()
        val show = ThatShow.createFromConfig()
        QlcShowFileGenerator.generate(path, show.lights)
        println("Generated file: $path")
        return
    }

    LightHub.run()
}

@ExperimentalTime
object LightHub {
    fun run() {
        val akai = Akai.findBestMatch() ?: throw RuntimeException("Midi unavailable!")

        if (!akai.open()) throw RuntimeException("Midi unavailable!")

        val show = ThatShow.createFromConfig()
        show.setController(akai)

        FlowTicker.runBlocking()
    }
}