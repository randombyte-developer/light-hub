package de.randombyte.lighthub.show

import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.midi.akai.Control
import de.randombyte.lighthub.osc.QlcPlus
import de.randombyte.lighthub.osc.devices.Device
import de.randombyte.lighthub.osc.devices.HexPar
import de.randombyte.lighthub.osc.devices.LedBar
import de.randombyte.lighthub.osc.devices.TsssPar
import de.randombyte.lighthub.osc.devices.features.Devices
import de.randombyte.lighthub.show.ThatShow.Mode.AMBIENT_MANUAL
import de.randombyte.lighthub.utils.Ranges

/**
 * One specific show with this many lights and the [Akai] as the controller.
 */
class ThatShow(
    val ledBar1: LedBar,
    val ledBar2: LedBar,
    val tsssPar1: TsssPar,
    val tsssPar2: TsssPar,
    val hexPar1: HexPar,
    val hexPar2: HexPar
) {

    companion object {
        fun createFromConfig(): ThatShow {
            val (ledBar1, ledBar2) = constructDevicesFromConfig(2, LedBar.Companion)
            val (tsssPar1, tsssPar2) = constructDevicesFromConfig(2, TsssPar.Companion)
            val (hexPar1, hexPar2) = constructDevicesFromConfig(2, HexPar.Companion)

            checkCollisions(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2)

            return ThatShow(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2)
        }

        private inline fun <reified T : Device> constructDevicesFromConfig(amount: Int, type: Device.Type<T>): List<T> {
            type.reloadConfigs()
            val addresses = type.metaConfigHolder.config.addresses
            if (addresses.size != amount) {
                throw RuntimeException("Exactly $amount addresses are needed for ${type.id}! ${addresses.size} addresses are set.")
            }
            return addresses.mapIndexed { index, address -> type.constructor(index, address) }
        }

        private fun checkCollisions(vararg devices: Device) {
            val collision = Devices.checkCollision(devices.toList())
            if (collision != null) {
                throw RuntimeException("DMX channels collide: $collision!")
            }
        }
    }

    val ledBars = listOf(ledBar1, ledBar2)
    val tsssPars = listOf(tsssPar1, tsssPar2)
    val adjPars = listOf(hexPar1, hexPar2)
    val lights = listOf(ledBars, adjPars, tsssPars).flatten()

    val ambientManual = AmbientManual(ledBars + tsssPars + adjPars)

    enum class Mode { AMBIENT_MANUAL }

    var mode = AMBIENT_MANUAL

    fun setController(akai: Akai) {
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
