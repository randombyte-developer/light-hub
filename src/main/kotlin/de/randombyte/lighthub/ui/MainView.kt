package de.randombyte.lighthub.ui

import javafx.application.Platform
import tornadofx.*

class MainView : View("LightHub") {
    override val root = borderpane {
        center = label("LightHub") {
            useMaxWidth = true
        }

        right = button("Exit") {
            action {
                Platform.exit()
            }
        }
    }
}