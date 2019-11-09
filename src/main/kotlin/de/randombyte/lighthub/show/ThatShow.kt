package de.randombyte.lighthub.show

import de.randombyte.lighthub.config.GlobalConfigs
import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.midi.akai.Akai.ControlName.*
import de.randombyte.lighthub.midi.akai.Control
import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.Devices
import de.randombyte.lighthub.osc.devices.*
import de.randombyte.lighthub.osc.devices.features.ShutterFeature
import de.randombyte.lighthub.show.flows.Flow
import de.randombyte.lighthub.show.flows.FlowManager
import de.randombyte.lighthub.show.flows.blackout.BlackoutFlow
import de.randombyte.lighthub.show.flows.colorchanger.ColorChangerFlow
import de.randombyte.lighthub.show.flows.colorchanger.ColorSetsConfig
import de.randombyte.lighthub.show.flows.manualcolor.ManualDeviceControl
import de.randombyte.lighthub.show.flows.pantilt.PanTiltFlow
import de.randombyte.lighthub.show.flows.rotation.RotationFlow
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow.Speed.Fast
import de.randombyte.lighthub.show.flows.strobe.StrobeFlow.Speed.Slow
import de.randombyte.lighthub.show.tickables.Tickable
import de.randombyte.lighthub.show.tickables.Ticker
import de.randombyte.lighthub.ui.events.ToggledMasterEvent
import de.randombyte.lighthub.ui.events.ToggledMasterEvent.MasterToggleDeviceCategory
import de.randombyte.lighthub.ui.events.ToggledMasterEvent.MasterToggleDeviceCategory.*
import de.randombyte.lighthub.utils.Ranges
import de.randombyte.lighthub.utils.flatten
import de.randombyte.lighthub.utils.pollForEach
import tornadofx.FX
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
    val hexPar2: HexPar,
    val hexClone1: HexClone,
    val hexClone2: HexClone,
    val quadPhase1: QuadPhase,
    val quadPhase2: QuadPhase,
    val scanner1: Scanner,
    val scanner2: Scanner,
    val scanner3: Scanner,
    val scanner4: Scanner
) {

    companion object {
        fun createShow(akai: Akai): ThatShow {
            val devices = Devices.createDevicesFromConfig()

            // todo: better
            return ThatShow(
                akai,
                devices[0] as LedBar,
                devices[1] as LedBar,
                devices[2] as TsssPar,
                devices[3] as TsssPar,
                devices[4] as HexPar,
                devices[5] as HexPar,
                devices[6] as HexClone,
                devices[7] as HexClone,
                devices[8] as QuadPhase,
                devices[9] as QuadPhase,
                devices[10] as Scanner,
                devices[11] as Scanner,
                devices[12] as Scanner,
                devices[13] as Scanner
            )
        }
    }

    // Device lists
    private val ledBars = listOf(ledBar1, ledBar2)
    private val tsssPars = listOf(tsssPar1, tsssPar2)
    private val hexPars = listOf(hexPar1, hexPar2)
    private val hexClones = listOf(hexClone1, hexClone2)
    private val quadPhases = listOf(quadPhase1, quadPhase2)
    private val scanners = listOf(scanner1, scanner2, scanner3, scanner4)

    private val lights = flatten<Device>(ledBars, tsssPars, hexPars, hexClones, quadPhases, scanners)

    // Flows and Tickables
    val manualDeviceControl = ManualDeviceControl(lights, sendDisplayName = akai::sendMapping)

    private val blackoutFlow = BlackoutFlow(lights as List<ShutterFeature>)
    private val colorChangeFlow = ColorChangerFlow(flatten(ledBars, tsssPars, hexPars, hexClones, quadPhases, scanners))
    private val rotationFlow = RotationFlow(quadPhases)
    private val panTiltFlow = PanTiltFlow(scanners)
    private val strobeFlow = StrobeFlow(flatten(ledBars, tsssPars, hexPars, hexClones, quadPhases, scanners))

    private val longTermFlows = listOf(colorChangeFlow)

    private lateinit var currentLongTermFlow: Flow<*>
    private var blockNewFlows = false
    fun activateFlow(flow: Flow<*>) {
        if (blockNewFlows) return
        if (flow in longTermFlows) {
            currentLongTermFlow = flow
        }
        FlowManager.requestDevices(flow)
    }

    private val masterToggle = MasterToggleDeviceCategory.values().map { it to true }.toMap().toMutableMap()

    init {
        registerTickables()
        registerControls()
        activateFlow(colorChangeFlow)

        // cause the event [ToggledMasterEvent] to be fired to set the color in the UI
        repeat(2) { MasterToggleDeviceCategory.values().forEach { toggleMaster(it) } }
    }

    private fun registerTickables() {
        Ticker.register(manualDeviceControl)
        Ticker.register(blackoutFlow)
        Ticker.register(colorChangeFlow)
        Ticker.register(rotationFlow)
        Ticker.register(panTiltFlow)
        Ticker.register(strobeFlow)

        FlowManager.registerIndependentFlow(rotationFlow)
        FlowManager.registerIndependentFlow(panTiltFlow)

        Ticker.register(object : Tickable {
            override fun onTick(tick: ULong) {
                akai.processCachedSignals()

                // run Runnables which were queued by other Threads to be executed on this Thread
                ShowThreadRunner.runnables.pollForEach { it.run() }
            }
        })
    }

    private fun registerControls() {
        // master dimmer
        akai.registerControl(MasterDimmer, object : Control.Potentiometer(6) {
            private var blackedOutBecauseOfMasterDimmer = false

            override fun onUpdate() {
                if (value == 0) {
                    activateFlow(blackoutFlow)
                    blackedOutBecauseOfMasterDimmer = true
                    blockNewFlows = true
                } else if (blackedOutBecauseOfMasterDimmer) {
                    blockNewFlows = false
                    activateFlow(currentLongTermFlow)
                    blackedOutBecauseOfMasterDimmer = false
                }
                QlcPlus.oscMasterDimmer.sendValue(Ranges.mapMidiToDmx(value))
                val percent = ((value.toDouble() / Ranges.MIDI_RANGE.last) * 100).toInt()
                akai.sendMapping("$percent%")
            }
        })

        akai.registerControl(Blackout, object : Control.Button.TouchButton(0) {
            override fun onDown() {
                activateFlow(blackoutFlow)
                blockNewFlows = true
            }

            override fun onUp() {
                blockNewFlows = false
                activateFlow(currentLongTermFlow)
            }
        })

        akai.registerControl(ColorChangeTempoFader, object : Control.Potentiometer(11) {
            override fun onUpdate() {
                val bpm = Ranges.mapRange(
                    from = Ranges.MIDI_RANGE,
                    to = GlobalConfigs.general.config.run { `bpm-fader-min`..`bpm-fader-max` },
                    value = value
                )

                Ticker.bpm = bpm
                akai.sendMapping("$bpm BPM")
            }
        })

        akai.registerControl(ColorChangeTransitionTicksFader, object : Control.Potentiometer(10) {
            override fun onUpdate() {
                val ticks = Ranges.mapRange(
                    from = Ranges.MIDI_RANGE,
                    to = GlobalConfigs.general.config.run { `transition-ticks-fader-min`..`transition-ticks-fader-max` },
                    value = value
                )

                colorChangeFlow.ticksTransitionDuration = ticks
                akai.sendMapping("$ticks Ticks")
            }
        })

        // color changer
        fun registerColorControl(controlName: Akai.ControlName, padNumber: Int, colorSetSelector: ColorSetsConfig.() -> List<String>) {
            akai.registerControl(controlName, object : Control.Button.TouchButton(padNumber) {
                override fun onDown() {
                    colorChangeFlow.colorSetSelector = colorSetSelector
                    activateFlow(colorChangeFlow)

                    // todo: separate control for rotation and pantilt
                    activateFlow(rotationFlow)
                    activateFlow(panTiltFlow)
                }
            })
        }

        registerColorControl(Set1, 12) { `set-1` }
        registerColorControl(Set2, 13) { `set-2` }
        registerColorControl(Set3, 14) { `set-3` }
        registerColorControl(Set4, 15) { `set-4` }
        registerColorControl(Set5, 8) { `set-5` }
        registerColorControl(Set6, 9) { `set-6` }
        registerColorControl(Set7, 10) { `set-7` }
        registerColorControl(Set8, 11) { `set-8` }
        registerColorControl(Set9, 4) { `set-9` }
        registerColorControl(Set10, 5) { `set-10` }
        registerColorControl(Set11, 6) { `set-11` }
        registerColorControl(Set12, 7) { `set-12` }

        // strobe
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

        // manual control
        fun registerManualControlKnob(controlName: Akai.ControlName, knobNumber: Int, action: ManualDeviceControl.(Control.Potentiometer) -> Unit) {
            akai.registerControl(controlName, object : Control.Potentiometer(knobNumber) {
                override fun onUpdate() {
                    manualDeviceControl.action(this)
                }
            })
        }

        registerManualControlKnob(Knob1, 4) { onKnob1ChangeValue(it) }
        registerManualControlKnob(Knob2, 2) { onKnob2ChangeValue(it) }
        registerManualControlKnob(Knob3, 0) { onKnob3ChangeValue(it) }
        registerManualControlKnob(Knob4, 5) { onKnob4ChangeValue(it) }
        registerManualControlKnob(Knob5, 3) { onKnob5ChangeValue(it) }
        registerManualControlKnob(Knob6, 1) { onKnob6ChangeValue(it) }

        akai.registerControl(ManualControlNext, object : Control.Button.SimpleButton(16) {
            override fun onDown() {
                manualDeviceControl.onSelectPreviousDevice()
            }
        })

        akai.registerControl(ManualControlPrevious, object : Control.Button.SimpleButton(17) {
            override fun onDown() {
                manualDeviceControl.onSelectNextDevice()
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

        // master toggle

        fun registerSimpleButton(controlName: Akai.ControlName, buttonNumber: Int, onPressed: () -> Unit) {
            akai.registerControl(controlName, object : Control.Button.SimpleButton(buttonNumber) {
                override fun onDown() {
                    onPressed()
                }
            })
        }

        registerSimpleButton(HexParsMasterToggle, 9) { toggleMaster(HexPars) }
        registerSimpleButton(OtherParsMasterToggle, 10) { toggleMaster(OtherPars) }
        registerSimpleButton(LedBarsMasterToggle, 11) { toggleMaster(LedBars) }
        registerSimpleButton(QuadsMasterToggle, 12) { toggleMaster(Quads) }
        registerSimpleButton(ScannerMasterToggle, 13) { toggleMaster(Scanners) }
    }

    fun toggleMaster(deviceCategory: MasterToggleDeviceCategory) {
        masterToggle[deviceCategory] = !masterToggle.getValue(deviceCategory)
        val activated = masterToggle.getValue(deviceCategory)

        lights
            .filter { it.type in deviceCategory.types }
            .forEach { device ->
                (device as ShutterFeature).noLight()
                if (activated) {
                    FlowManager.freeDevice(device)
                    activateFlow(currentLongTermFlow)
                } else {
                    FlowManager.claimDevice(device)
                }
        }

        FX.eventbus.fire(ToggledMasterEvent(activated, deviceCategory))
    }
}