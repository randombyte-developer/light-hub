package de.randombyte.lighthub.show

import de.randombyte.lighthub.keyboard.KeyControl
import de.randombyte.lighthub.keyboard.Keyboard
import de.randombyte.lighthub.show.masterflows.OneLightOnly
import de.randombyte.lighthub.show.masterflows.SawToothIntensity
import de.randombyte.lighthub.ui.events.ToggledMasterEvent.MasterToggleDeviceCategory.*
import javafx.scene.Node
import javafx.scene.input.KeyEvent
import kotlin.time.ExperimentalTime

@ExperimentalTime
object KeyboardControls {

    val masterToggles = mapOf(
        "1" to HexPars,
        "2" to OtherPars,
        "3" to LedBars,
        "4" to Quads,
        "5" to Scanners
    )

    val masterFlows = mapOf(
        "G" to OneLightOnly,
        "Z" to SawToothIntensity
    )

    fun init(sceneGraphNode: Node) {
        sceneGraphNode.addEventHandler(KeyEvent.KEY_PRESSED, Keyboard)

        masterToggles.forEach { (key, masterToggleDeviceCategory) ->
            Keyboard.register(key, object : KeyControl {
                override fun onPressed() {
                    ThatShow.toggleMaster(masterToggleDeviceCategory)
                }
            })
        }

        masterFlows.forEach { (key, masterFlow) ->
            Keyboard.register(key, object : KeyControl {
                override fun onPressed() {
                    MasterFlowManager.activate(masterFlow)
                }
            })
        }
    }
}