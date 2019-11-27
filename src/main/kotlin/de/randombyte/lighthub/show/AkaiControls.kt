package de.randombyte.lighthub.show

import de.randombyte.lighthub.config.GlobalConfigs
import de.randombyte.lighthub.midi.akai.Akai
import de.randombyte.lighthub.midi.akai.Akai.ControlName.*
import de.randombyte.lighthub.midi.akai.Control
import de.randombyte.lighthub.osc.devices.QlcPlus
import de.randombyte.lighthub.show.DevicesManager.scanners
import de.randombyte.lighthub.show.flows.colorchanger.ColorChangerFlow
import de.randombyte.lighthub.show.quickeffects.Blackout
import de.randombyte.lighthub.show.quickeffects.Strobe
import de.randombyte.lighthub.show.quickeffects.Strobe.Speed.Fast
import de.randombyte.lighthub.show.quickeffects.Strobe.Speed.Slow
import de.randombyte.lighthub.show.tickables.Ticker
import de.randombyte.lighthub.ui.events.ToggledMasterEvent.MasterToggleDeviceCategory.*
import de.randombyte.lighthub.utils.Ranges
import kotlin.time.ExperimentalTime

@ExperimentalTime
object AkaiControls {

    var nullableAkai: Akai? = null

    val akai: Akai get() = nullableAkai ?: throw IllegalStateException("Akai is not initilaized!")

    fun init(): Boolean {
        nullableAkai = Akai.findBestMatch {
            nullableAkai = null
        } ?: return false
        if (!nullableAkai!!.open()) return false

        registerControls()
        return true
    }

    private fun registerControls() {
        // master dimmer
        akai.registerControl(MasterDimmer, object : Control.Potentiometer(6) {
            private var blackedOutBecauseOfMasterDimmer = false

            override fun onUpdate() {
                if (value == 0) {
                    ThatShow.blackout()

                    blackedOutBecauseOfMasterDimmer = true
                    FlowManager.blockFlowChanges = true
                } else if (blackedOutBecauseOfMasterDimmer) {
                    FlowManager.blockFlowChanges = false
                    blackedOutBecauseOfMasterDimmer = false

                    MasterFlowManager.activateFallback()
                }

                QlcPlus.oscMasterDimmer.sendValue(Ranges.mapMidiToDmx(value))
                val percent = ((value.toDouble() / Ranges.MIDI_RANGE.last) * 100).toInt()
                akai.sendMapping("$percent%")
            }
        })

        akai.registerControl(Akai.ControlName.Blackout, object : Control.Button.TouchButton(0) {
            override fun onDown() {
                Blackout.activate()
            }

            override fun onUp() {
                Blackout.deactivate()
            }
        })

        akai.registerControl(TempoFader, object : Control.Potentiometer(11) {
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

                ColorChangerFlow.ticksTransitionDuration = ticks
                akai.sendMapping("$ticks Ticks")
            }
        })

        // color changer
        fun registerColorControl(controlName: Akai.ControlName, padNumber: Int, colorSet: String) {
            akai.registerControl(controlName, object : Control.Button.TouchButton(padNumber) {
                override fun onDown() {
                    ColorSelector.selectedColorSetId = colorSet
                }
            })
        }

        registerColorControl(Set1, 12, "set-1")
        registerColorControl(Set2, 13, "set-2")
        registerColorControl(Set3, 14, "set-3")
        registerColorControl(Set4, 15, "set-4")
        registerColorControl(Set5, 8, "set-5")
        registerColorControl(Set6, 9, "set-6")
        registerColorControl(Set7, 10, "set-7")
        registerColorControl(Set8, 11, "set-8")
        registerColorControl(Set9, 4, "set-9")
        registerColorControl(Set10, 5, "set-10")
        registerColorControl(Set11, 6, "set-11")
        registerColorControl(Set12, 7, "set-12")

        // strobe
        akai.registerControl(SlowStrobe, object : Control.Button.TouchButton(2) {
            override fun onDown() {
                ThatShow.blackout(scanners)
                Strobe.speed = Slow
                Strobe.activate()
            }

            override fun onUp() {
                // if not the other strobe button is pressed
                if (akai.getControlByName(FastStrobe)?.value?.isPressed != true) {
                    Strobe.deactivate()
                }
            }
        })

        akai.registerControl(FastStrobe, object : Control.Button.TouchButton(3) {
            override fun onDown() {
                ThatShow.blackout(scanners)
                Strobe.speed = Fast
                Strobe.activate()
            }

            override fun onUp() {
                if (akai.getControlByName(SlowStrobe)?.value?.isPressed != true) {
                    Strobe.deactivate()
                }
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

        registerSimpleButton(HexParsMasterToggle, 9) { ThatShow.toggleMaster(HexPars) }
        registerSimpleButton(OtherParsMasterToggle, 10) { ThatShow.toggleMaster(OtherPars) }
        registerSimpleButton(LedBarsMasterToggle, 11) { ThatShow.toggleMaster(LedBars) }
        registerSimpleButton(QuadsMasterToggle, 12) { ThatShow.toggleMaster(Quads) }
        registerSimpleButton(ScannerMasterToggle, 13) { ThatShow.toggleMaster(Scanners) }
    }
}