package de.randombyte.lighthub.ui

import javafx.stage.Stage
import tornadofx.App

class LightHubApp : App(MainView::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.isFullScreen = true
    }
}