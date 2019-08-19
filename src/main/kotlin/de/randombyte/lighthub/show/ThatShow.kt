package de.randombyte.lighthub.show

import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.midi.akai.Control
import de.randombyte.lighthub.osc.OscChannelList
import de.randombyte.lighthub.osc.QlcPlus
import de.randombyte.lighthub.osc.devices.Device
import de.randombyte.lighthub.osc.devices.HexPar
import de.randombyte.lighthub.osc.devices.LedBar
import de.randombyte.lighthub.osc.devices.TsssPar
import de.randombyte.lighthub.osc.devices.features.Devices
import de.randombyte.lighthub.osc.devices.features.StrobeFeature
import de.randombyte.lighthub.show.ThatShow.Mode.AMBIENT_MANUAL
import de.randombyte.lighthub.utils.Ranges
import de.randombyte.lighthub.utils.flatten

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
            type.metaConfigHolder.reload()
            val addresses = type.metaConfigHolder.config.addresses
            if (addresses.size != amount) {
                throw RuntimeException("Exactly $amount addresses are needed for ${type.id}! ${addresses.size} addresses are set.")
            }

            val devices = addresses.mapIndexed { index, address -> type.constructor(index, address) }
            devices.forEach { it.reloadConfigs() }

            return devices
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

    val lights = flatten<Device>(ledBars, adjPars, tsssPars)
    val lightsWithUniqueId = lights.associateBy { it.uniqueId }

    val strobeLights = flatten<StrobeFeature>(ledBars, adjPars, tsssPars)

    val ambientManual = AmbientManual((ledBars + tsssPars + adjPars) as List<Device>)

    enum class Mode { AMBIENT_MANUAL }

    private var mode = AMBIENT_MANUAL

    private var snapshot: MutableMap<String, OscChannelList.Snapshot> = mutableMapOf()

    fun setController(akai: Akai) {

        // master dimmer
        akai.registerControl(object : Control.Potentiometer(6) {
            override fun onUpdate() {
                QlcPlus.oscMasterDimmer.sendValue(Ranges.mapMidiToDmx(value))
            }
        })

        // blackout
        akai.registerControl(object : Control.Button.TouchButton(0) {
            override fun onDown() {
                QlcPlus.oscBlackout.sendValue(1)
            }

            override fun onUp() {
                // simulate a "flash" button, the real QLC+ is actually a toggle button
                QlcPlus.oscBlackout.sendValue(1)
            }
        })

        // strobe
        akai.registerControl(object : Control.Button.TouchButton(2) {
            override fun onDown() {
                if (!saveSnapshot()) return
                strobeLights.forEach { it.slowStrobe() }
            }

            override fun onUp() {
                restoreSnapshot()
            }
        })

        akai.registerControl(object : Control.Button.TouchButton(3) {
            override fun onDown() {
                if (!saveSnapshot()) return
                strobeLights.forEach { it.fastStrobe() }
            }

            override fun onUp() {
                restoreSnapshot()
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

    /**
     * @return false if there is already a snapshot, true if saving the snapshot was successful
     */
    private fun saveSnapshot(): Boolean {
        if (snapshot.isNotEmpty()) return false
        lightsWithUniqueId.forEach { (id, light) -> snapshot[id] = light.oscChannelList.snapshot }
        return true
    }

    private fun restoreSnapshot() {
        snapshot.forEach { (id, snapshot) -> lightsWithUniqueId.getValue(id).oscChannelList.restore(snapshot) }
        snapshot.clear()
    }
}