package de.randombyte.lighthub.midi.akai

import de.randombyte.lighthub.config.GlobalConfigs
import de.randombyte.lighthub.midi.MidiHandler
import de.randombyte.lighthub.midi.Signal
import de.randombyte.lighthub.midi.akai.Control.Button.TouchButton
import de.randombyte.lighthub.utils.pollForEach
import java.util.concurrent.ConcurrentLinkedQueue
import javax.sound.midi.*

/**
 * Specifically the Akai MPD26. The listener will only react to the special SysEx messages enabled with [enableSpecialMode].
 */
class Akai(inDevice: MidiDevice, outDevice: MidiDevice, val onClose: () -> Unit) : MidiHandler(inDevice, outDevice) {

    companion object {
        const val NAME = "MPD26"

        val MIDI_PAD_NUMBERS = 36..51
        val SYSEX_PAD_NUMBERS = 0..15
        private const val MIDI_ON = 0x90.toByte()
        private const val MIDI_OFF = 0x80.toByte()

        fun findBestMatch(onClose: () -> Unit): Akai? = try {
            val devices = MidiSystem.getMidiDeviceInfo().map { MidiSystem.getMidiDevice(it) }

            val inDevice = devices.firstOrNull { "MidiInDevice" in it.javaClass.simpleName && NAME in it.deviceInfo.name }
            val outDevice = devices.firstOrNull { "MidiOutDevice" in it.javaClass.simpleName && NAME in it.deviceInfo.name }

            if (inDevice != null && outDevice != null) Akai(inDevice, outDevice, onClose) else null
        } catch (ex: MidiUnavailableException) {
            null
        }
    }

    // todo: better, nesting?
    enum class ControlName {
        Blackout, MasterDimmer, SlowStrobe, FastStrobe,
        VeryFastBpm, UltraSlowBpm,
        Set1, Set2, Set3, Set4, Set5, Set6, Set7, Set8, Set9, Set10, Set11, Set12,
        TempoFader, ColorChangeTransitionTicksFader,
        HexParsMasterToggle, OtherParsMasterToggle, LedBarsMasterToggle, QuadsMasterToggle, ScannerMasterToggle
    }

    private val controls = mutableMapOf<ControlName, Control>()

    private val signalsCache = ConcurrentLinkedQueue<Signal>()

    override fun open(): Boolean {
        if (!super.open()) return false
        enableSpecialMode()
        setupListener()
        sendMapping("LightHub")
        return true
    }

    fun registerControl(controlName: ControlName, control: Control) {
        controls[controlName] = control
    }

    fun isControlPressed(controlName: ControlName) = (getControlByName(controlName) as? Control.Button)?.isPressed == true

    fun getControlByName(controlName: ControlName) = controls[controlName]

    private fun setupListener() {
        inDevice.transmitter.receiver = object : Receiver {
            override fun send(message: MidiMessage, timestamp: Long) {
                val data = message.message
                val signal = when (data.size) {
                    3 -> parseNormalMidiIfPad(data)
                    10 -> SysEx.parseSysExIfNotPad(message.message)
                    else -> return
                } ?: return

                signalsCache.add(signal)
            }

            override fun close() {
                onClose()
            }
        }
    }

    fun processCachedSignals() {
        signalsCache.pollForEach { signal ->
            val control = findControl(signal)
            control?.update(signal.value)
        }
    }

    /**
     * If normal midi signals are detected, use those for the touch buttons because these messages are faster than the
     * SysEx ones. Sometimes SysEx only sends zeros when you are really fast. Because of that we use normal midi
     * messages for those instead. But we will still return a [Signal] with the values a SysEx message would have.
     */
    private fun parseNormalMidiIfPad(data: ByteArray): Signal? {
        if (data.size != 3) return null
        if (data[1].toInt() !in MIDI_PAD_NUMBERS) return null
        if (data[0] != MIDI_ON && data[0] != MIDI_OFF) return null

        return Signal(type = TouchButton.SYSEX_TYPE, control = data[1].toInt() - MIDI_PAD_NUMBERS.first, value = data[2].toInt())
    }

    private fun findControl(signal: Signal) = controls.values.find { control ->
        control.type == signal.type && control.number == signal.control
    }

    /**
     * Sends a special SysEx message to the device to make it send every button press, even those that aren't mapped to
     * any midi signal. This is the mode the official editor operates in.
     */
    private fun enableSpecialMode() {
        sendSysEx(SysEx.SYSEX_SPECIAL_MODE)
    }

    fun sendMapping(name: String) {
        if (!GlobalConfigs.general.config.`akai-send-display-info`) return
        outDevice.receiver.send(SysexMessage(SysEx.createMappingWithName(name), SysEx.MAPPING_LENGTH), -1)
    }
}