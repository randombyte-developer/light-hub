package de.randombyte.lighthub.show

import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.midi.akai.Akai.ControlName.*
import de.randombyte.lighthub.midi.akai.Control
import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.HexPar
import de.randombyte.lighthub.osc.devices.LedBar
import de.randombyte.lighthub.osc.devices.QlcPlus
import de.randombyte.lighthub.osc.devices.TsssPar
import de.randombyte.lighthub.osc.devices.features.*
import de.randombyte.lighthub.show.flows.Flow
import de.randombyte.lighthub.show.flows.FlowManager
import de.randombyte.lighthub.show.flows.blackout.BlackoutFlow
import de.randombyte.lighthub.show.flows.colorchanger.ColorChangerFlow
import de.randombyte.lighthub.show.flows.manualcolor.ManualDeviceControl
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow.Speed.Fast
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow.Speed.Slow
import de.randombyte.lighthub.show.tickables.Tickable
import de.randombyte.lighthub.show.tickables.Ticker
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

            checkChannels(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2)
            checkCollisions(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2)
            checkStrobeColor(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2)
            checkColorCategories(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2)

            return ThatShow(ledBar1, ledBar2, tsssPar1, tsssPar2, hexPar1, hexPar2)
        }

        private fun <T : Device> constructDevicesFromConfig(amount: Int, type: Device.Type<T>): List<T> {
            type.metaConfig.reload()
            type.reloadConfigs()

            val addresses = type.metaConfig.config.addresses
            if (addresses.size != amount) {
                throw RuntimeException("Exactly $amount addresses are needed for ${type.id}! ${addresses.size} addresses are set.")
            }

            val devices = addresses.mapIndexed { index, address -> type.constructor(index, address) }
            return devices
        }

        private fun checkChannels(vararg devices: Device) {
            devices.forEach { device ->
                require(device.type.channelsCount == device.oscChannelList.allNestedChannels.size) {
                    "[${device.type.id}] ${device.type.channelsCount} channels are defined, but " +
                            "actually ${device.oscChannelList.allNestedChannels.size} are implemented!"
                }
            }
        }

        private fun checkCollisions(vararg devices: Device) {
            val collision = Devices.checkCollision(devices.toList())
            require(collision == null) { "DMX channels collide: $collision!" }
        }

        private fun checkStrobeColor(vararg devices: RgbFeature) {
            devices.forEach { device ->
                require(device.colorCategories.strobe.isNotBlank()) {
                    "[${device.type.id}] Strobe color is not set in the color categories config!"
                }
            }
        }

        private fun checkColorCategories(vararg devices: RgbFeature) {
            devices.forEach { device ->
                device.colorCategories.all.forEach { (categoryId, colorsIds) ->
                    colorsIds.forEach { colorId ->
                        require(colorId in device.colors.keys) {
                            "[${device.type.id}] Color '$colorId' is defined in the color category '$categoryId' " +
                                    "but is missing in the color definitions of the device!"
                        }
                    }
                }
            }
        }

        private const val STROBE_COLOR = "white"
    }

    // Device lists
    val ledBars = listOf(ledBar1, ledBar2)
    val tsssPars = listOf(tsssPar1, tsssPar2)
    val adjPars = listOf(hexPar1, hexPar2)

    val colorLights = flatten<ColorFeature>(ledBars, adjPars, tsssPars)

    val lights = flatten<Device>(ledBars, adjPars, tsssPars)

    val strobeLights = flatten(ledBars, adjPars, tsssPars)
        .requireInstanceOf<StrobeFeature, DimmableComponentsColorFeature>()

    // Flows and Tickables
    val manualDeviceControl = ManualDeviceControl((ledBars + tsssPars + adjPars) as List<Device>)

    private val blackoutFlow = BlackoutFlow(lights as List<MasterDimmerFeature>)
    private val colorChangeFlow = ColorChangerFlow(colorLights)
    private val strobeFlow = StrobeFlow(strobeLights)

    private val longTermFlows = listOf(colorChangeFlow)

    private lateinit var currentLongTermFlow: Flow<*>
    fun activateFlow(flow: Flow<*>) {
        if (flow in longTermFlows) {
            currentLongTermFlow = flow
        }
        FlowManager.requestDevices(flow)
    }

    init {
        Ticker.register(manualDeviceControl)
        Ticker.register(blackoutFlow)
        Ticker.register(colorChangeFlow)
        Ticker.register(strobeFlow)
    }

    fun setController(akai: Akai) {
        // todo: better
        Ticker.register(object : Tickable {
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
                val min = (Ticker.TICKS_PER_SECOND * 0.25).toInt()
                val max = (Ticker.TICKS_PER_SECOND * 10).toInt()
                val tempo = akai.getControlByName(ColorChangeTempoFader)!!.value.coerceIn(min..max)

                colorChangeFlow.tempo = tempo
                colorChangeFlow.ticksUntilColorChange = tempo
            }
        })

        akai.registerControl(ColorChangeActivate, object : Control.Button.TouchButton(12) {
            override fun onDown() {
                activateFlow(colorChangeFlow)
            }
        })

        /*akai.registerControl(ColorChangeInstant, object : Control.Button.TouchButton(13) {
            override fun onDown() {
                colorChangeFlow.forceColorChangeOnThisTick()
            }
        })*/

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

        // manual knobs
        akai.registerControl(Knob1, object : Control.Potentiometer(4) {
            override fun onUpdate() {
                manualDeviceControl.onKnob1ChangeValue(this)
            }
        })
        akai.registerControl(Knob2, object : Control.Potentiometer(2) {
            override fun onUpdate() {
                manualDeviceControl.onKnob2ChangeValue(this)
            }
        })
        akai.registerControl(Knob3, object : Control.Potentiometer(0) {
            override fun onUpdate() {
                manualDeviceControl.onKnob3ChangeValue(this)
            }
        })
        akai.registerControl(Knob4, object : Control.Potentiometer(5) {
            override fun onUpdate() {
                manualDeviceControl.onKnob4ChangeValue(this)
            }
        })
        akai.registerControl(Knob5, object : Control.Potentiometer(3) {
            override fun onUpdate() {
                manualDeviceControl.onKnob5ChangeValue(this)
            }
        })
        akai.registerControl(Knob6, object : Control.Potentiometer(1) {
            override fun onUpdate() {
                manualDeviceControl.onKnob6ChangeValue(this)
            }
        })

        akai.registerControl(ManualControlNext, object : Control.Button.SimpleButton(16) {
            override fun onDown() {
                manualDeviceControl.onSelectPreviousDevice()
                akai.sendMapping(name = manualDeviceControl.device.shortNameForDisplay)
            }
        })

        akai.registerControl(ManualControlPrevious, object : Control.Button.SimpleButton(17) {
            override fun onDown() {
                manualDeviceControl.onSelectNextDevice()
                akai.sendMapping(name = manualDeviceControl.device.shortNameForDisplay)
            }
        })

        akai.registerControl(ManualControlClaim, object : Control.Button.SimpleButton(18) {
            override fun onDown() {
                manualDeviceControl.onClaimDevice()
            }
        })

        akai.registerControl(ManualControlFree, object : Control.Button.SimpleButton(19) {
            override fun onDown() {
                manualDeviceControl.onFreeDevice()
            }
        })
    }
}