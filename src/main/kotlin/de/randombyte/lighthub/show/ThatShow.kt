package de.randombyte.lighthub.show

import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.midi.akai.Control
import de.randombyte.lighthub.osc.QlcPlus
import de.randombyte.lighthub.osc.dmx.AdjPar
import de.randombyte.lighthub.osc.dmx.LedBar
import de.randombyte.lighthub.osc.dmx.TsssPar
import de.randombyte.lighthub.show.ThatShow.Mode.AMBIENT_MANUAL
import de.randombyte.lighthub.utils.Ranges

/**
 * One specific show with this many lights and the [Akai] as the controller.
 */
object ThatShow {

    val ledBar1 = LedBar(number = 1, startAddress = 29)
    val ledBars = listOf(ledBar1)

    val tsssPar1 = TsssPar(number = 1, startAddress = 43)
    val tsssPar2 = TsssPar(number = 2, startAddress = 51)
    val tsssPars = listOf(tsssPar1, tsssPar2)

    val adjPar1 = AdjPar(number = 1, startAddress = 65)
    val adjPar2 = AdjPar(number = 2, startAddress = 77)
    val adjPars = listOf(adjPar1, adjPar2)

    val lights = listOf(ledBars, adjPars, tsssPars).flatten()


    enum class Mode { AMBIENT_MANUAL }
    var mode = AMBIENT_MANUAL

    val ambientManual = AmbientManual(ledBars, tsssPars, adjPars)


    fun setup(akai: Akai) {
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
                adjPars.forEach { it.dimmingMode() }
                tsssPars.forEach { it.dimmingMode() }
                ambientManual.selectNext()
                val selectedLight = ambientManual.get().type.configHolder.config.meta.`short-name` + "/" +
                        ambientManual.get().number.toString()
                akai.sendMapping(selectedLight)
            }
        })
    }
}
