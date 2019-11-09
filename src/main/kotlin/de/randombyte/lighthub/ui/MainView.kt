package de.randombyte.lighthub.ui

import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.show.ShowThreadRunner
import de.randombyte.lighthub.show.ThatShow
import de.randombyte.lighthub.show.tickables.Ticker
import de.randombyte.lighthub.ui.events.ToggledMasterEvent
import de.randombyte.lighthub.ui.events.ToggledMasterEvent.MasterToggleDeviceCategory.*
import javafx.concurrent.Task
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MainView : View("LightHub") {

    lateinit var hexParsLabel: Label
    lateinit var otherParsLabel: Label
    private lateinit var barsLabel: Label
    private lateinit var quadsLabel: Label
    private lateinit var scannersLabel: Label

    lateinit var statusLabel: Label

    private val labelDeviceCategoryMapping by lazy {
            mapOf(
            HexPars to hexParsLabel,
            OtherPars to otherParsLabel,
            LedBars to barsLabel,
            Quads to quadsLabel,
            Scanners to scannersLabel
        )
    }

    private lateinit var asyncTask: Task<*>

    private lateinit var show: ThatShow

    override val root = vbox {
        hbox {
            hexParsLabel = squareLabelWithCenteredText("Hex")
            otherParsLabel = squareLabelWithCenteredText("Pars")
            barsLabel = squareLabelWithCenteredText("Bars")
            quadsLabel = squareLabelWithCenteredText("Quads")
            scannersLabel = squareLabelWithCenteredText("Scanner")
        }

        spacer {
            prefHeight = 10.0
        }

        statusLabel = label("LightHub") {
            style {
                fontWeight = FontWeight.BOLD
            }
        }

        subscribe<ToggledMasterEvent> { event ->
            val backgroundColor = if (event.activated) Color.DARKSEAGREEN else Color.LIGHTGOLDENRODYELLOW
            val background = Background((BackgroundFill(backgroundColor, null, null)))
            labelDeviceCategoryMapping.getValue(event.deviceCategory).background = background
        }

        addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            ShowThreadRunner.runLater {
                when (event.code.char) {
                    "1" -> show.toggleMaster(HexPars)
                    "2" -> show.toggleMaster(OtherPars)
                    "3" -> show.toggleMaster(LedBars)
                    "4" -> show.toggleMaster(Quads)
                    "5" -> show.toggleMaster(Scanners)
                }
            }
        }
    }

    override fun onDock() {
        root.requestFocus()

         asyncTask = runAsync {
            val akai = Akai.findBestMatch()
            if (akai == null) {
                runLater {
                    statusLabel.text = "Akai not found!"
                }
            } else {
                if (!akai.open()) {
                    runLater {
                        statusLabel.text("Akai interface couldn't be opened!")
                    }
                } else {
                    show = ThatShow.createShow(akai)
                    Ticker.runBlocking()
                }
            }
        }
    }

    override fun onUndock() {
        exitProcess(0)
    }

    private fun EventTarget.squareLabelWithCenteredText(text: String, length: Double = 100.0) = label(text) {
        prefWidth = length
        prefHeight = length
        alignment = Pos.CENTER
        style {
            borderColor += box(Color.BLACK)
        }
    }
}