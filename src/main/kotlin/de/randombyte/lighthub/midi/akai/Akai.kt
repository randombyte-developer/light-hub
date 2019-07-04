package de.randombyte.lighthub.midi.akai

import de.randombyte.lighthub.midi.MidiHandler
import javax.sound.midi.*

/**
 * Specifically the Akai MPD26. The listener will only react to the special SysEx messages enabled with [enableSpecialMode].
 */
class Akai(inDevice: MidiDevice, outDevice: MidiDevice) : MidiHandler(inDevice, outDevice) {

    companion object {
        const val NAME = "MPD26"

        fun findBestMatch(): Akai? = try {
            val devices = MidiSystem.getMidiDeviceInfo().map { MidiSystem.getMidiDevice(it) }

            val inDevice = devices.firstOrNull { "MidiInDevice" in it.javaClass.simpleName && NAME in it.deviceInfo.name }
            val outDevice = devices.firstOrNull { "MidiOutDevice" in it.javaClass.simpleName && NAME in it.deviceInfo.name }

            if (inDevice != null && outDevice != null) Akai(inDevice, outDevice) else null
        } catch (ex: MidiUnavailableException) {
            null
        }
    }

    private val controls: MutableList<Control> = mutableListOf()

    override fun open(): Boolean {
        if (!super.open()) return false
        enableSpecialMode()
        setupListener()
        sendMapping("QLC+")
        return true
    }

    fun registerControl(control: Control) {
        controls += control
    }

    private fun setupListener() {
        inDevice.transmitter.receiver = object : Receiver {
            override fun send(message: MidiMessage, timestamp: Long) {
                val signal = SysEx.parseSysEx(message.message) ?: return
                println(signal)
                val control = findControl(signal) ?: return
                control.update(signal.value)
            }

            override fun close() {
                println("Closing")
            }
        }
    }

    private fun findControl(signal: SysEx.Signal) = controls.find { control ->
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
        outDevice.receiver.send(SysexMessage(SysEx.createMappingWithName(name), SysEx.MAPPING_LENGTH), -1)
    }
}