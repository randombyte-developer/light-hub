package de.randombyte.lighthub.keyboard

import de.randombyte.lighthub.show.ShowThreadRunner
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import kotlin.time.ExperimentalTime

@ExperimentalTime
object Keyboard : EventHandler<KeyEvent> {

    private val controls: MutableMap<String, KeyControl> = mutableMapOf()

    fun register(key: String, control: KeyControl) {
        controls[key] = control
    }

    override fun handle(event: KeyEvent) {
        val keyControl = controls[event.code.char] ?: return

        ShowThreadRunner.runLater {
            keyControl.onPressed()
        }
    }
}