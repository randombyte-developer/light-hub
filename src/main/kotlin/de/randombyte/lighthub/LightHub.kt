package de.randombyte.lighthub

import de.randombyte.lighthub.config.GlobalConfigs
import de.randombyte.lighthub.qlc.QlcShowFileGenerator
import de.randombyte.lighthub.show.DevicesManager
import de.randombyte.lighthub.ui.LightHubApp
import tornadofx.launch
import java.nio.file.Paths
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main(args: Array<String>) {
    GlobalConfigs.init()

    if (args.getOrNull(0) == "gen-qlc") {
        val path = Paths.get("LightHubShow.qxw").toAbsolutePath()
        DevicesManager.init()
        QlcShowFileGenerator.generate(path, DevicesManager.lights)
        println("Generated file: $path")
        return
    }

    launch<LightHubApp>()
}