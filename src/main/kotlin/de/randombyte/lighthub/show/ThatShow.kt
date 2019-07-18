package de.randombyte.lighthub.show

import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.midi.akai.Control
import de.randombyte.lighthub.osc.QlcPlus
import de.randombyte.lighthub.osc.devices.HexPar
import de.randombyte.lighthub.osc.devices.LedBar
import de.randombyte.lighthub.osc.devices.TsssPar
import de.randombyte.lighthub.show.ThatShow.Mode.AMBIENT_MANUAL
import de.randombyte.lighthub.utils.Ranges

/**
 * One specific show with this many lights and the [Akai] as the controller.
 */
class ThatShow {

    val ledBar1 = LedBar(number = 1)
    val ledBar2 = LedBar(number = 2)
    val ledBars = listOf(ledBar1, ledBar2)

    val tsssPar1 = TsssPar(number = 1)
    val tsssPar2 = TsssPar(number = 2)
    val tsssPars = listOf(tsssPar1, tsssPar2)

    val adjPar1 = HexPar(number = 1)
    val adjPar2 = HexPar(number = 2)
    val adjPars = listOf(adjPar1, adjPar2)

    val lights = listOf(ledBars, adjPars, tsssPars).flatten()

    init {
        setupLights()
    }

    enum class Mode { AMBIENT_MANUAL }
    var mode = AMBIENT_MANUAL

    lateinit var ambientManual: AmbientManual

    fun setupLights() {
        lights.forEach { it.reloadConfigs() }
    }

    fun setup(akai: Akai) {
        setupLights()
        ambientManual = AmbientManual(ledBars + tsssPars + adjPars)
        setupController(akai)
    }

    private fun setupController(akai: Akai) {
        akai.registerControl(object : Control.Button.TouchButton(0) {
            override fun onUp() {
                // make the toggle button to a "flash" button
                QlcPlus.oscBlackout.sendValue(1)
                QlcPlus.oscBlackout.sendValue(0)
            }

            override fun onDown() {
                QlcPlus.oscBlackout.sendValue(1)
                QlcPlus.oscBlackout.sendValue(0)
            }
        })

        akai.registerControl(object : Control.Potentiometer(6) {
            override fun onUpdate() {
                QlcPlus.oscMasterDimmer.sendValue(Ranges.mapMidiToDmx(value))
            }
        })

        // manual knobs
        akai.registerControl(object : Control.Potentiometer(4) {
            override fun onUpdate() {
                if (mode == AMBIENT_MANUAL) ambientManual.plusRed(direction)
            }
        })
        akai.registerControl(object : Control.Potentiometer(2) {
            override fun onUpdate() {
                if (mode == AMBIENT_MANUAL) ambientManual.plusGreen(direction)
            }
        })
        akai.registerControl(object : Control.Potentiometer(0) {
            override fun onUpdate() {
                if (mode == AMBIENT_MANUAL) ambientManual.plusBlue(direction)
            }
        })
        akai.registerControl(object : Control.Potentiometer(5) {
            override fun onUpdate() {
                if (mode == AMBIENT_MANUAL) ambientManual.plusWhite(direction)
            }
        })
        akai.registerControl(object : Control.Potentiometer(3) {
            override fun onUpdate() {
                if (mode == AMBIENT_MANUAL) ambientManual.plusAmber(direction)
            }
        })
        akai.registerControl(object : Control.Potentiometer(1) {
            override fun onUpdate() {
                if (mode == AMBIENT_MANUAL) ambientManual.plusUv(direction)
            }
        })

        // manual ambient switch
        akai.registerControl(object : Control.Button.SimpleButton(20) {
            override fun onDown() {
                ledBars.forEach { it.ledOn() }
                adjPars.forEach { it.dimmingMode() }
                tsssPars.forEach { it.dimmingMode() }
                val selectedDeviceName = ambientManual.selectNextDevice()
                akai.sendMapping(selectedDeviceName)
            }
        })
    }
}
