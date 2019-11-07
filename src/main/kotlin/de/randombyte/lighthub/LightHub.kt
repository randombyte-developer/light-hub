package de.randombyte.lighthub

import de.randombyte.lighthub.config.GlobalConfigs
import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.osc.Devices
import de.randombyte.lighthub.qlc.QlcShowFileGenerator
import de.randombyte.lighthub.show.ThatShow
import de.randombyte.lighthub.show.tickables.Ticker
import de.randombyte.lighthub.ui.LightHubApp
import tornadofx.launch
import java.nio.file.Paths
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main(args: Array<String>) {
    GlobalConfigs.reload()

    if (args.getOrNull(0) == "gen-qlc") {
        val path = Paths.get("LightHubShow.qxw").toAbsolutePath()
        val lights = Devices.createDevicesFromConfig()
        QlcShowFileGenerator.generate(path, lights)
        println("Generated file: $path")
        return
    }

    launch<LightHubApp>()

    //LightHub.run()
}

@ExperimentalTime
object LightHub {
    fun run() {
        val akai = Akai.findBestMatch() ?: throw RuntimeException("Midi unavailable!")

        if (!akai.open()) throw RuntimeException("Midi unavailable!")

        val show = ThatShow.createShow(akai)
        Ticker.runBlocking()
    }
}