package de.randombyte.lighthub.show

import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.midi.akai.Akai.ControlName.*
import de.randombyte.lighthub.midi.akai.Control
import de.randombyte.lighthub.osc.QlcPlus
import de.randombyte.lighthub.osc.devices.Device
import de.randombyte.lighthub.osc.devices.HexPar
import de.randombyte.lighthub.osc.devices.LedBar
import de.randombyte.lighthub.osc.devices.TsssPar
import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.show.ThatShow.Mode.AMBIENT_MANUAL
import de.randombyte.lighthub.show.flows.Flow
import de.randombyte.lighthub.show.flows.FlowTicker
import de.randombyte.lighthub.show.flows.blackout.BlackoutFlow
import de.randombyte.lighthub.show.flows.colorchanger.ColorChangerFlow
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow.Speed.Fast
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow.Speed.Slow
import de.randombyte.lighthub.utils.Ranges
import de.randombyte.lighthub.utils.flatten
import de.randombyte.lighthub.utils.requireInstanceOf
import kotlin.time.ExperimentalTime

/**
 * One specific show with this many lights and the [Akai] as the controller.
 */
@ExperimentalTime
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
            checkStrobeColor(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2)
            checkColorCategories(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2)

            return ThatShow(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2)
        }

        private fun <T : Device> constructDevicesFromConfig(amount: Int, type: Device.Type<T>): List<T> {
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

        private fun checkStrobeColor(vararg devices: RgbFeature) {
            devices.forEach { device ->
                if (STROBE_COLOR !in device.colors.keys) {
                    throw RuntimeException("Strobe color '$STROBE_COLOR' is missing in ${device.type.id}!")
                }
            }
        }

        private fun checkColorCategories(vararg devices: RgbFeature) {
            devices.forEach { device ->
                device.colorCategories.all.forEach { (categoryId, colorsIds) ->
                    colorsIds.forEach { colorId ->
                        if (colorId !in device.colors.keys) {
                            throw RuntimeException(
                                "[${device.type.id}] Color '$colorId' is defined in the color category '$categoryId' " +
                                        "but is missing in the color definitions of the device!")
                        }
                    }
                }
            }
        }

        private const val STROBE_COLOR = "white"
    }

    val ledBars = listOf(ledBar1, ledBar2)
    val tsssPars = listOf(tsssPar1, tsssPar2)
    val adjPars = listOf(hexPar1, hexPar2)

    val colorLights = flatten<ColorFeature>(ledBars, adjPars, tsssPars)

    val lights = flatten<Device>(ledBars, adjPars, tsssPars)

    val strobeLights = flatten(ledBars, adjPars, tsssPars)
        .requireInstanceOf<StrobeFeature, DimmableComponentsColorFeature>()

    val ambientManual = AmbientManual((ledBars + tsssPars + adjPars) as List<Device>)

    enum class Mode { AMBIENT_MANUAL }

    private var mode = AMBIENT_MANUAL

    private val blackoutFlow = BlackoutFlow(lights as List<MasterDimmerFeature>)
    private val colorChangeFlow = ColorChangerFlow(colorLights)
    private val strobeFlow = StrobeFlow(strobeLights)

    private val longTermFlows = listOf(colorChangeFlow)

    private lateinit var currentLongTermFlow: Flow<*>
    fun activateFlow(flow: Flow<*>) {
        if (flow in longTermFlows) {
            currentLongTermFlow = flow
        }
        FlowTicker.activate(flow)
    }

    init {
        FlowTicker.registerFlows(blackoutFlow, colorChangeFlow, strobeFlow)
        activateFlow(colorChangeFlow)
    }

    fun setController(akai: Akai) {
        FlowTicker.activate(object : Flow<Any>(assignedDevices = emptyList()) {
            override fun onTick() {
                akai.processCachedSignals()
            }
        })

        // master dimmer
        akai.registerControl(MasterDimmer, object : Control.Potentiometer(6) {
            override fun onUpdate() {
                QlcPlus.oscMasterDimmer.sendValue(Ranges.mapMidiToDmx(value))
            }
        })

        akai.registerControl(Blackout, object : Control.Button.TouchButton(0) {
            override fun onDown() {
                activateFlow(blackoutFlow)
            }

            override fun onUp() {
                activateFlow(currentLongTermFlow)
            }
        })

        akai.registerControl(ColorChangeTempoFader, object : Control.Potentiometer(11) {
            override fun onUpdate() {
                // todo: better with config
                val min = (FlowTicker.TICKS_PER_SECOND * 0.25).toInt()
                val max = (FlowTicker.TICKS_PER_SECOND * 10).toInt()
                val tempo = akai.getControlByName(ColorChangeTempoFader)!!.value.coerceIn(min..max)

                colorChangeFlow.tempo = tempo
                colorChangeFlow.ticksUntilColorChange = tempo
            }
        })

        akai.registerControl(ColorChangeInstant, object : Control.Button.TouchButton(12) {
            override fun onDown() {
                colorChangeFlow.forceColorChangeOnThisTick()
            }
        })

        akai.registerControl(SlowStrobe, object : Control.Button.TouchButton(2) {
            override fun onDown() {
                strobeFlow.speed = Slow
                activateFlow(strobeFlow)
            }

            override fun onUp() {
                // if not the other strobe button is pressed
                if (akai.getControlByName(FastStrobe)?.value?.isPressed != true) {
                    activateFlow(currentLongTermFlow)
                }
            }
        })

        akai.registerControl(FastStrobe, object : Control.Button.TouchButton(3) {
            override fun onDown() {
                strobeFlow.speed = Fast
                activateFlow(strobeFlow)
            }

            override fun onUp() {
                if (akai.getControlByName(SlowStrobe)?.value?.isPressed != true) {
                    activateFlow(currentLongTermFlow)
                }
            }
        })
/*
        // manual knobs
        akai.registerControl(Knob1, object : Control.Potentiometer(4) {
            override fun onUpdate() {
                if (mode == AMBIENT_MANUAL) ambientManual.plusRed(direction)
            }
        })
        akai.registerControl(Knob2, object : Control.Potentiometer(2) {
            override fun onUpdate() {
                if (mode == AMBIENT_MANUAL) ambientManual.plusGreen(direction)
            }
        })
        akai.registerControl(Knob3, object : Control.Potentiometer(0) {
            override fun onUpdate() {
                if (mode == AMBIENT_MANUAL) ambientManual.plusBlue(direction)
            }
        })
        akai.registerControl(Knob4, object : Control.Potentiometer(5) {
            override fun onUpdate() {
                if (mode == AMBIENT_MANUAL) ambientManual.plusWhite(direction)
            }
        })
        akai.registerControl(Knob5, object : Control.Potentiometer(3) {
            override fun onUpdate() {
                if (mode == AMBIENT_MANUAL) ambientManual.plusAmber(direction)
            }
        })
        akai.registerControl(Knob6, object : Control.Potentiometer(1) {
            override fun onUpdate() {
                if (mode == AMBIENT_MANUAL) ambientManual.plusUv(direction)
            }
        })

        // manual ambient switch
        akai.registerControl(AmbientManualSwitch, object : Control.Button.SimpleButton(20) {
            override fun onDown() {
                val selectedDevice = ambientManual.selectNextDevice()
                akai.sendMapping(name = selectedDevice.shortNameForDisplay)
            }
        })*/
    }
}