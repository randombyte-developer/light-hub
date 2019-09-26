package de.randombyte.lighthub.show

import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.midi.akai.Akai.ControlName.*
import de.randombyte.lighthub.midi.akai.Control
import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.Devices
import de.randombyte.lighthub.osc.devices.HexPar
import de.randombyte.lighthub.osc.devices.LedBar
import de.randombyte.lighthub.osc.devices.QlcPlus
import de.randombyte.lighthub.osc.devices.TsssPar
import de.randombyte.lighthub.osc.devices.features.ColorFeature
import de.randombyte.lighthub.osc.devices.features.DimmableComponentsColorFeature
import de.randombyte.lighthub.osc.devices.features.MasterDimmerFeature
import de.randombyte.lighthub.osc.devices.features.StrobeFeature
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
    val akai: Akai,
    val ledBar1: LedBar,
    val ledBar2: LedBar,
    val tsssPar1: TsssPar,
    val tsssPar2: TsssPar,
    val hexPar1: HexPar,
    val hexPar2: HexPar
) {

    companion object {
        fun createShow(akai: Akai) {
            val devices = Devices.createDevicesFromConfig()

            // todo: better
            ThatShow(
                akai,
                devices[0] as LedBar,
                devices[1] as LedBar,
                devices[2] as TsssPar,
                devices[3] as TsssPar,
                devices[4] as HexPar,
                devices[5] as HexPar
            )
        }
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
    val manualDeviceControl = ManualDeviceControl((ledBars + tsssPars + adjPars) as List<Device>, sendDisplayName = akai::sendMapping)

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
        registerTickables()
        registerControls()
    }

    fun registerTickables() {
        Ticker.register(manualDeviceControl)
        Ticker.register(blackoutFlow)
        Ticker.register(colorChangeFlow)
        Ticker.register(strobeFlow)

        Ticker.register(object : Tickable {
            override fun onTick() {
                akai.processCachedSignals()
            }
        })
    }

    fun registerControls() {
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